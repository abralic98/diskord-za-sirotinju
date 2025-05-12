
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
    sessionUserMap.put(session, "temp"); // Temporary placeholder
    // Send current presence for all rooms
    rooms.forEach((roomId, peers) -> {
      if (peers.contains(session)) {
        broadcastPresence(roomId);
      }
    });
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    Map<String, Object> data = mapper.readValue(message.getPayload(), Map.class);
    String type = (String) data.get("type");
    String room = (String) data.get("room");

    if (room == null || type == null)
      return;

    Map<String, Object> sender = (Map<String, Object>) data.get("sender");
    String userId = (String) sender.get("id");
    if (userId == null)
      return;

    sessionUserMap.put(session, userId);
    userRoomMap.put(userId, room);

    rooms.computeIfAbsent(room, k -> new ArrayList<>());
    List<WebSocketSession> peers = rooms.get(room);

    switch (type) {
      case "getPresence":
        // Immediately send presence for the requested room
        broadcastPresence(room);
        break;

      case "join":
        // Remove from all rooms first
        rooms.forEach((r, sessions) -> {
          if (sessions.remove(session)) {
            broadcastPresence(r);
          }
        });

        if (!peers.contains(session)) {
          peers.add(session);
          broadcastPresence(room);
        }
        break;

      case "offer":
      case "answer":
      case "candidate":
        for (WebSocketSession peer : peers) {
          if (!peer.equals(session) && peer.isOpen()) {
            peer.sendMessage(new TextMessage(mapper.writeValueAsString(data)));
          }
        }
        break;
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    System.out.println("🔴 Disconnected: " + session.getId());

    String userId = sessionUserMap.remove(session);
    if (userId != null) {
      String room = userRoomMap.remove(userId);
      if (room != null) {
        rooms.getOrDefault(room, Collections.emptyList()).remove(session);
        broadcastPresence(room);
      }
    }
  }

  private void broadcastPresence(String roomId) {
    if ("all".equals(roomId)) {
      // Special case: send presence for all rooms
      rooms.keySet().forEach(this::sendRoomPresence);
    } else {
      sendRoomPresence(roomId);
    }
  }

  private void sendRoomPresence(String roomId) {
    List<WebSocketSession> peers = rooms.getOrDefault(roomId, Collections.emptyList());
    List<String> userIds = new ArrayList<>();

    for (WebSocketSession peer : peers) {
      String uid = sessionUserMap.get(peer);
      if (uid != null)
        userIds.add(uid);
    }

    Map<String, Object> payload = new HashMap<>();
    payload.put("type", "presence");
    payload.put("room", roomId);
    payload.put("users", userIds);

    try {
      String json = mapper.writeValueAsString(payload);
      // Send to ALL connected clients, not just room members
      for (WebSocketSession session : sessionUserMap.keySet()) {
        if (session.isOpen()) {
          session.sendMessage(new TextMessage(json));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
