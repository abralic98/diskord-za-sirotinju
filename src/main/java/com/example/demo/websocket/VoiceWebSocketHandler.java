
package com.example.demo.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VoiceWebSocketHandler extends TextWebSocketHandler {

  private final Map<String, List<WebSocketSession>> rooms = new ConcurrentHashMap<>();
  private final Map<WebSocketSession, String> sessionUserMap = new ConcurrentHashMap<>();
  private final Map<String, String> userRoomMap = new ConcurrentHashMap<>();
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    System.out.println("🟢 Connected: " + session.getId());
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    System.out.println("📨 Received: " + message.getPayload());
    Map<String, Object> data = mapper.readValue(message.getPayload(), Map.class);
    String type = (String) data.get("type");
    String room = (String) data.get("room");

    if (room == null)
      return;

    // Extract user ID from sender object
    Map<String, Object> sender = (Map<String, Object>) data.get("sender");
    String userId = (String) sender.get("id");

    if (userId == null)
      return;

    // Remove session from all rooms
    rooms.forEach((r, sessions) -> sessions.remove(session));

    // Add session to the new room
    rooms.computeIfAbsent(room, k -> new ArrayList<>());
    if (!rooms.get(room).contains(session)) {
      rooms.get(room).add(session);
    }

    // Associate session with user ID
    sessionUserMap.put(session, userId);

    // Send updated user list to everyone in the room
    broadcastUserList(room);

    // Forward message to all others in the room
    for (WebSocketSession peer : rooms.get(room)) {
      if (!peer.equals(session) && peer.isOpen()) {
        peer.sendMessage(message);
      }
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    System.out.println("🔴 Closed: " + session.getId());

    sessionUserMap.remove(session);

    for (Map.Entry<String, List<WebSocketSession>> entry : rooms.entrySet()) {
      String room = entry.getKey();
      List<WebSocketSession> sessions = entry.getValue();

      if (sessions.remove(session)) {
        broadcastUserList(room);
        break;
      }
    }
  }

  private void broadcastUserList(String room) throws Exception {
    List<WebSocketSession> sessions = rooms.getOrDefault(room, new ArrayList<>());

    List<String> userIds = new ArrayList<>();
    for (WebSocketSession s : sessions) {
      String uid = sessionUserMap.get(s);
      if (uid != null)
        userIds.add(uid);
    }

    Map<String, Object> response = new HashMap<>();
    response.put("type", "user-list");
    response.put("users", userIds);
    String msg = mapper.writeValueAsString(response);

    for (WebSocketSession s : sessions) {
      if (s.isOpen()) {
        s.sendMessage(new TextMessage(msg));
      }
    }
  }
}
