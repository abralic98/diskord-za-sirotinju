
package com.example.demo.websocket;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

public class VoiceWebSocketHandler extends TextWebSocketHandler {

  private final ObjectMapper objectMapper = new ObjectMapper();

  // roomId -> Map<session, userId>
  private final Map<String, Map<WebSocketSession, String>> roomSessions = new ConcurrentHashMap<>();

  // session -> roomId
  private final Map<WebSocketSession, String> sessionRoom = new ConcurrentHashMap<>();

  // session -> userId
  private final Map<WebSocketSession, String> sessionUser = new ConcurrentHashMap<>();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    // No action yet, wait for join message
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
    JsonNode node = objectMapper.readTree(message.getPayload());

    String type = node.get("type").asText();

    switch (type) {
      case "join":
        handleJoin(session, node);
        break;

      case "offer":
      case "answer":
      case "candidate":
        handleSignal(session, node);
        break;

      default:
        System.out.println("Unknown message type: " + type);
    }
  }

  private void handleJoin(WebSocketSession session, JsonNode node) throws IOException {
    String roomId = node.get("room").asText();
    String userId = node.get("userId").asText();

    // Save session info
    sessionRoom.put(session, roomId);
    sessionUser.put(session, userId);

    roomSessions.putIfAbsent(roomId, new ConcurrentHashMap<>());
    Map<WebSocketSession, String> sessionsInRoom = roomSessions.get(roomId);
    sessionsInRoom.put(session, userId);

    // Send existing users to the new user (excluding self)
    List<String> otherUsers = new ArrayList<>();
    for (String uid : sessionsInRoom.values()) {
      if (!uid.equals(userId)) {
        otherUsers.add(uid);
      }
    }

    Map<String, Object> existingUsersMsg = new HashMap<>();
    existingUsersMsg.put("type", "existingUsers");
    existingUsersMsg.put("users", otherUsers);

    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(existingUsersMsg)));

    System.out.println("User " + userId + " joined room " + roomId);
  }

  private void handleSignal(WebSocketSession session, JsonNode node) throws IOException {
    String roomId = sessionRoom.get(session);
    String userId = sessionUser.get(session);

    String receiverId = node.get("receiver").get("id").asText();

    Map<WebSocketSession, String> sessionsInRoom = roomSessions.get(roomId);
    if (sessionsInRoom == null) {
      System.out.println("Room not found: " + roomId);
      return;
    }

    // Find session of receiver
    WebSocketSession receiverSession = null;
    for (Map.Entry<WebSocketSession, String> entry : sessionsInRoom.entrySet()) {
      if (entry.getValue().equals(receiverId)) {
        receiverSession = entry.getKey();
        break;
      }
    }

    if (receiverSession != null && receiverSession.isOpen()) {
      // Forward signaling message to the receiver
      receiverSession.sendMessage(new TextMessage(node.toString()));
    } else {
      System.out.println("Receiver session not found or closed: " + receiverId);
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    String roomId = sessionRoom.remove(session);
    String userId = sessionUser.remove(session);

    if (roomId != null) {
      Map<WebSocketSession, String> sessionsInRoom = roomSessions.get(roomId);
      if (sessionsInRoom != null) {
        sessionsInRoom.remove(session);
        if (sessionsInRoom.isEmpty()) {
          roomSessions.remove(roomId);
        }
      }
    }

    System.out.println("User " + userId + " disconnected from room " + roomId);
  }
}
