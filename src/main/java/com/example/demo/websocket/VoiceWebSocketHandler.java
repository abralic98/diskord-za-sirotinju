
package com.example.demo.websocket;

import com.example.demo.dto.user.SocketUserDTO;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class VoiceWebSocketHandler extends TextWebSocketHandler {

  // serverId -> (roomId -> list of sessions)
  private final Map<String, Map<String, List<WebSocketSession>>> servers = new ConcurrentHashMap<>();

  // serverId -> all sessions connected to server (regardless of room)
  private final Map<String, List<WebSocketSession>> serverSessions = new ConcurrentHashMap<>();

  // session -> userId
  private final Map<WebSocketSession, Long> sessionUserIdMap = new ConcurrentHashMap<>();

  // userId -> serverId
  private final Map<Long, String> userServerMap = new ConcurrentHashMap<>();

  // userId -> roomId
  private final Map<Long, String> userRoomMap = new ConcurrentHashMap<>();

  private final ObjectMapper mapper = new ObjectMapper();
  private final UserRepository userRepository;

  public VoiceWebSocketHandler(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    System.out.println("Connected: " + session.getId());
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    Map<String, Object> data = mapper.readValue(message.getPayload(), Map.class);
    String type = (String) data.get("type");

    switch (type) {
      case "joinServer":
        handleJoinServer(session, data);
        break;

      case "joinRoom":
        handleJoinRoom(session, data);
        break;

      case "leaveRoom":
        handleLeaveRoom(session);
        break;

      case "getPresence":
        handleGetPresence(session, data);
        break;

      default:
        System.out.println("Unknown message type: " + type);
    }
  }

  private void handleJoinServer(WebSocketSession session, Map<String, Object> data) {
    String serverId = (String) data.get("server");
    Long userId = parseUserId(data.get("userId"));

    if (userId == null || serverId == null)
      return;

    sessionUserIdMap.put(session, userId);
    userServerMap.put(userId, serverId);

    servers.putIfAbsent(serverId, new ConcurrentHashMap<>());

    // Add session to serverSessions map
    serverSessions.putIfAbsent(serverId, Collections.synchronizedList(new ArrayList<>()));
    List<WebSocketSession> sessions = serverSessions.get(serverId);
    if (!sessions.contains(session)) {
      sessions.add(session);
    }
  }

  private void handleJoinRoom(WebSocketSession session, Map<String, Object> data) {
    Long userId = sessionUserIdMap.get(session);
    if (userId == null)
      return;

    String serverId = userServerMap.get(userId);
    if (serverId == null)
      return;

    String roomId = (String) data.get("room");
    if (roomId == null)
      return;

    servers.putIfAbsent(serverId, new ConcurrentHashMap<>());
    Map<String, List<WebSocketSession>> roomMap = servers.get(serverId);

    // Ensure room list exists, synchronized list for thread safety
    roomMap.putIfAbsent(roomId, Collections.synchronizedList(new ArrayList<>()));

    // Remove user session from all other rooms in the same server
    roomMap.values().forEach(sessions -> sessions.remove(session));

    // Add user session to new room
    roomMap.get(roomId).add(session);
    userRoomMap.put(userId, roomId);

    broadcastServerPresence(serverId);
  }

  private void handleLeaveRoom(WebSocketSession session) {
    Long userId = sessionUserIdMap.get(session);
    if (userId == null)
      return;

    String serverId = userServerMap.get(userId);
    String roomId = userRoomMap.remove(userId);

    if (serverId == null || roomId == null)
      return;

    Map<String, List<WebSocketSession>> roomMap = servers.getOrDefault(serverId, Collections.emptyMap());
    List<WebSocketSession> sessions = roomMap.getOrDefault(roomId, Collections.synchronizedList(new ArrayList<>()));

    sessions.remove(session);

    broadcastServerPresence(serverId);
  }

  private void handleGetPresence(WebSocketSession session, Map<String, Object> data) {
    String serverId = (String) data.get("serverId");
    Map<String, Object> sender = (Map<String, Object>) data.get("sender");
    Long userId = sender != null ? parseUserId(sender.get("id")) : null;

    if (userId != null && serverId != null) {
      sessionUserIdMap.put(session, userId);
      userServerMap.put(userId, serverId);
      servers.putIfAbsent(serverId, new ConcurrentHashMap<>());

      // Add session to serverSessions map if missing
      serverSessions.putIfAbsent(serverId, Collections.synchronizedList(new ArrayList<>()));
      List<WebSocketSession> sessions = serverSessions.get(serverId);
      if (!sessions.contains(session)) {
        sessions.add(session);
      }
    }

    broadcastServerPresence(serverId);
  }

  private Long parseUserId(Object userIdObj) {
    try {
      if (userIdObj instanceof Number) {
        return ((Number) userIdObj).longValue();
      } else if (userIdObj instanceof String) {
        return Long.parseLong((String) userIdObj);
      }
    } catch (NumberFormatException ignored) {
    }
    return null;
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    System.out.println("Disconnected: " + session.getId());

    Long userId = sessionUserIdMap.remove(session);
    if (userId == null)
      return;

    String serverId = userServerMap.remove(userId);
    String roomId = userRoomMap.remove(userId);

    // Remove session from serverSessions map
    if (serverId != null) {
      List<WebSocketSession> sessions = serverSessions.get(serverId);
      if (sessions != null) {
        sessions.remove(session);
      }
    }

    if (serverId == null || roomId == null)
      return;

    Map<String, List<WebSocketSession>> roomMap = servers.getOrDefault(serverId, Collections.emptyMap());
    List<WebSocketSession> sessions = roomMap.getOrDefault(roomId, Collections.synchronizedList(new ArrayList<>()));

    sessions.remove(session);

    broadcastServerPresence(serverId);
  }

  private void broadcastServerPresence(String serverId) {
    if (serverId == null)
      return;

    Map<String, List<WebSocketSession>> roomMap = servers.getOrDefault(serverId, new ConcurrentHashMap<>());

    List<Map<String, Object>> roomsData = new ArrayList<>();
    roomMap.forEach((roomId, sessions) -> {
      List<SocketUserDTO> users = sessions.stream()
          .map(sessionUserIdMap::get)
          .filter(Objects::nonNull)
          .map(userRepository::findById)
          .filter(Optional::isPresent)
          .map(opt -> new SocketUserDTO(opt.get()))
          .collect(Collectors.toList());

      Map<String, Object> roomObj = new HashMap<>();
      roomObj.put("roomId", roomId);
      roomObj.put("users", users);
      roomsData.add(roomObj);
    });

    if (roomsData.isEmpty()) {
      roomsData.add(Map.of("roomId", "default", "users", List.of()));
    }

    Map<String, Object> payload = Map.of(
        "type", "serverPresence",
        "serverId", serverId,
        "rooms", roomsData);

    try {
      String json = mapper.writeValueAsString(payload);

      // Send presence update to ALL sessions connected to the server
      List<WebSocketSession> allSessions = serverSessions.getOrDefault(serverId, Collections.emptyList());

      allSessions.stream()
          .filter(WebSocketSession::isOpen)
          .forEach(session -> {
            try {
              session.sendMessage(new TextMessage(json));
            } catch (Exception e) {
              e.printStackTrace();
            }
          });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
