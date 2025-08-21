
package com.example.demo.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VoiceWebSocketHandler extends TextWebSocketHandler {

  private final ObjectMapper json = new ObjectMapper();

  // roomId -> (session -> userId)
  private final Map<String, Map<WebSocketSession, String>> roomSessions = new ConcurrentHashMap<>();
  // roomId -> (userId -> session) for O(1) direct routing
  private final Map<String, Map<String, WebSocketSession>> roomUsers = new ConcurrentHashMap<>();

  // session -> roomId / userId
  private final Map<WebSocketSession, String> sessionRoom = new ConcurrentHashMap<>();
  private final Map<WebSocketSession, String> sessionUser = new ConcurrentHashMap<>();

  // Optional room capacity (mesh gets expensive quickly)
  private static final int MAX_ROOM_SIZE = 8;

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    // Wait for explicit join
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
    JsonNode node;
    try {
      node = json.readTree(message.getPayload());
    } catch (Exception e) {
      send(session, error("badJson", e.getMessage()));
      return;
    }

    final String type = text(node, "type");
    if (type == null) {
      send(session, error("missingType", "Missing 'type'"));
      return;
    }

    switch (type) {
      case "join":
        handleJoin(session, node);
        break;
      case "offer":
      case "answer":
      case "candidate":
        handleSignal(session, node, type);
        break;
      case "leave":
        handleLeave(session);
        break;
      case "ping":
        send(session, pong());
        break;
      default:
        send(session, error("unknownType", type));
    }
  }

  private void handleJoin(WebSocketSession session, JsonNode node) throws IOException {
    final String roomId = text(node, "room");
    final String userIdRaw = text(node, "userId");
    if (roomId == null || userIdRaw == null) {
      send(session, error("missingFields", "'room' and 'userId' are required"));
      return;
    }

    // Initialize structures
    roomSessions.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>());
    roomUsers.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>());
    final Map<WebSocketSession, String> sessions = roomSessions.get(roomId);
    final Map<String, WebSocketSession> users = roomUsers.get(roomId);

    // Capacity check
    if (sessions.size() >= MAX_ROOM_SIZE) {
      send(session, error("roomFull", "Room capacity reached"));
      return;
    }

    // Enforce unique userId in room: disconnect prior or make a new unique id
    String userId = userIdRaw;
    if (users.containsKey(userIdRaw)) {
      // policy: kick old tab
      WebSocketSession old = users.get(userIdRaw);
      if (old != null && old.isOpen()) {
        try {
          old.close(CloseStatus.POLICY_VIOLATION);
        } catch (Exception ignored) {
        }
      }
      cleanupSession(old); // will remove from maps
    }

    // Bind maps
    sessionRoom.put(session, roomId);
    sessionUser.put(session, userId);
    sessions.put(session, userId);
    users.put(userId, session);

    // Tell the new user who is already there
    ObjectNode existing = json.createObjectNode();
    existing.put("type", "existingUsers");
    ArrayNode arr = existing.putArray("users");
    sessions.values().stream().filter(uid -> !uid.equals(userId)).forEach(arr::add);
    send(session, existing);

    // ACK the join with canonical self id
    ObjectNode joinedAck = json.createObjectNode();
    joinedAck.put("type", "joined");
    joinedAck.put("room", roomId);
    joinedAck.putObject("self").put("id", userId);
    send(session, joinedAck);

    // Notify others
    ObjectNode joined = json.createObjectNode();
    joined.put("type", "userJoined");
    joined.put("user", userId);
    broadcastExcept(roomId, session, joined);

    log("join", "user=%s room=%s size=%d", userId, roomId, sessions.size());
  }

  private void handleSignal(WebSocketSession session, JsonNode node, String kind) throws IOException {
    String roomId = sessionRoom.get(session);
    String fromUser = sessionUser.get(session);
    if (roomId == null || fromUser == null) {
      send(session, error("notInRoom", "Join a room before signaling"));
      return;
    }

    JsonNode recv = node.get("receiver");
    String receiverId = (recv != null && recv.get("id") != null) ? recv.get("id").asText() : null;
    if (receiverId == null) {
      send(session, error("missingReceiver", "Missing receiver.id"));
      return;
    }

    WebSocketSession target = roomUsers.getOrDefault(roomId, Map.of()).get(receiverId);
    if (target == null || !target.isOpen()) {
      send(session, error("receiverUnavailable", receiverId));
      return;
    }

    // Forward with sender info
    ObjectNode out = node.deepCopy();
    ObjectNode sender = out.with("sender");
    sender.put("id", fromUser);
    out.put("type", kind);
    send(target, out);
  }

  private void handleLeave(WebSocketSession session) throws IOException {
    cleanupSession(session);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    cleanupSession(session);
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    // Close on error to trigger cleanup
    try {
      session.close(CloseStatus.SERVER_ERROR);
    } catch (Exception ignored) {
    }
  }

  private void cleanupSession(WebSocketSession session) throws IOException {
    if (session == null)
      return;

    String roomId = sessionRoom.remove(session);
    String userId = sessionUser.remove(session);
    if (roomId == null)
      return;

    Map<WebSocketSession, String> sessions = roomSessions.get(roomId);
    Map<String, WebSocketSession> users = roomUsers.get(roomId);

    if (sessions != null)
      sessions.remove(session);
    if (users != null && userId != null)
      users.remove(userId);

    // delete empty room maps
    if (sessions != null && sessions.isEmpty())
      roomSessions.remove(roomId);
    if (users != null && users.isEmpty())
      roomUsers.remove(roomId);

    if (userId != null) {
      ObjectNode left = json.createObjectNode();
      left.put("type", "userLeft");
      left.put("user", userId);
      broadcast(roomId, left);
      log("leave", "user=%s room=%s", userId, roomId);
    }
  }

  // ---------- helpers ----------
  private void broadcast(String roomId, JsonNode msg) throws IOException {
    Map<WebSocketSession, String> sessions = roomSessions.get(roomId);
    if (sessions == null)
      return;
    String payload = json.writeValueAsString(msg);
    for (WebSocketSession s : sessions.keySet()) {
      if (s.isOpen())
        s.sendMessage(new TextMessage(payload));
    }
  }

  private void broadcastExcept(String roomId, WebSocketSession except, JsonNode msg) throws IOException {
    Map<WebSocketSession, String> sessions = roomSessions.get(roomId);
    if (sessions == null)
      return;
    String payload = json.writeValueAsString(msg);
    for (WebSocketSession s : sessions.keySet()) {
      if (s != except && s.isOpen())
        s.sendMessage(new TextMessage(payload));
    }
  }

  private void send(WebSocketSession s, JsonNode n) throws IOException {
    if (s != null && s.isOpen())
      s.sendMessage(new TextMessage(json.writeValueAsString(n)));
  }

  private static String text(JsonNode n, String f) {
    JsonNode v = (n == null) ? null : n.get(f);
    return (v != null && v.isTextual()) ? v.asText() : null;
  }

  private static ObjectNode error(String code, String msg) {
    ObjectMapper m = new ObjectMapper();
    ObjectNode o = m.createObjectNode();
    o.put("type", "error");
    o.put("code", code);
    o.put("message", msg);
    return o;
  }

  private static ObjectNode pong() {
    ObjectMapper m = new ObjectMapper();
    ObjectNode o = m.createObjectNode();
    o.put("type", "pong");
    o.put("ts", Instant.now().toEpochMilli());
    return o;
  }

  private static void log(String tag, String fmt, Object... args) {
    System.out.println("[VoiceWS][" + tag + "] " + String.format(fmt, args));
  }
}
