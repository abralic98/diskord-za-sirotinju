package com.example.demo.websocket;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VoiceWebSocketHandler extends TextWebSocketHandler {

  private final Map<String, List<WebSocketSession>> rooms = new ConcurrentHashMap<>();
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

    rooms.putIfAbsent(room, new ArrayList<>());
    if (!rooms.get(room).contains(session)) {
      rooms.get(room).add(session);
    }

    // Broadcast to other users in the same room
    for (WebSocketSession peer : rooms.get(room)) {
      if (!peer.equals(session) && peer.isOpen()) {
        peer.sendMessage(message);
      }
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    System.out.println("🔴 Closed: " + session.getId());
    rooms.values().forEach(list -> list.remove(session));
  }

}

