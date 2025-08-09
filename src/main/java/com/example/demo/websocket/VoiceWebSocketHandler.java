
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
      case "joinServer": {
        String serverId = (String) data.get("server");
        Long userId = Long.parseLong(data.get("userId").toString());

        sessionUserIdMap.put(session, userId);
        userServerMap.put(userId, serverId);

        servers.putIfAbsent(serverId, new ConcurrentHashMap<>());
        break;
      }
      case "joinRoom": {
        Long userId = sessionUserIdMap.get(session);
        String serverId = userServerMap.get(userId);
        String roomId = (String) data.get("room");

        servers.putIfAbsent(serverId, new ConcurrentHashMap<>());
        servers.get(serverId).putIfAbsent(roomId, Collections.synchronizedList(new ArrayList<>()));

        // Remove from all rooms in same server
        servers.get(serverId).forEach((rid, sessions) -> sessions.remove(session));

        // Add to new room
        servers.get(serverId).get(roomId).add(session);
        userRoomMap.put(userId, roomId);

        broadcastServerPresence(serverId);
        break;
      }
      case "leaveRoom": {
        Long userId = sessionUserIdMap.get(session);
        String serverId = userServerMap.get(userId);
        String roomId = userRoomMap.remove(userId);
        System.out.println("User " + userId + " leaving room " + roomId);

        if (serverId != null && roomId != null) {
          List<WebSocketSession> sessions = servers
              .getOrDefault(serverId, Map.of())
              .getOrDefault(roomId, Collections.synchronizedList(new ArrayList<>()));
          sessions.remove(session);
          System.out.println("Updated room sessions: " + sessions);

          broadcastServerPresence(serverId);
        }
        break;
      }
      case "offer":
      case "answer":
      case "candidate": {
        Long userId = sessionUserIdMap.get(session);
        String serverId = userServerMap.get(userId);
        String roomId = userRoomMap.get(userId);

        for (WebSocketSession peer : servers
            .getOrDefault(serverId, Map.of())
            .getOrDefault(roomId, List.of())) {
          if (!peer.equals(session) && peer.isOpen()) {
            peer.sendMessage(new TextMessage(mapper.writeValueAsString(data)));
          }
        }
        break;
      }
      case "getPresence": {
        Long userId = sessionUserIdMap.get(session);
        String serverId = userServerMap.get(userId);
        broadcastServerPresence(serverId);
        break;
      }
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    System.out.println("Disconnected: " + session.getId());

    Long userId = sessionUserIdMap.remove(session);
    if (userId != null) {
      String serverId = userServerMap.remove(userId);
      String roomId = userRoomMap.remove(userId);

      if (serverId != null && roomId != null) {
        List<WebSocketSession> sessions = servers
            .getOrDefault(serverId, Map.of())
            .getOrDefault(roomId, Collections.synchronizedList(new ArrayList<>()));
        sessions.remove(session);

        broadcastServerPresence(serverId);
      }
    }
  }

  private void broadcastServerPresence(String serverId) {
    Map<String, List<WebSocketSession>> roomMap = servers.getOrDefault(serverId, Map.of());

    List<Map<String, Object>> roomsData = new ArrayList<>();
    roomMap.forEach((roomId, sessions) -> {
      List<SocketUserDTO> users = sessions.stream()
          .map(sessionUserIdMap::get)
          .map(userRepository::findById)
          .filter(Optional::isPresent)
          .map(opt -> new SocketUserDTO(opt.get()))
          .collect(Collectors.toList());

      Map<String, Object> roomObj = new HashMap<>();
      roomObj.put("roomId", roomId);
      roomObj.put("users", users);
      roomsData.add(roomObj);
    });

    Map<String, Object> payload = new HashMap<>();
    payload.put("type", "serverPresence");
    payload.put("serverId", serverId);
    payload.put("rooms", roomsData);

    try {
      String json = mapper.writeValueAsString(payload);
      roomMap.values().stream()
          .flatMap(List::stream)
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
