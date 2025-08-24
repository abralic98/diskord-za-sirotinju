
package com.example.demo.service.room;

import com.example.demo.dto.rooms.RoomWithUsersDTO;
import com.example.demo.dto.server.ServerVoiceRoomsDTO;
import com.example.demo.dto.user.SocketUserDTO;
import com.example.demo.helpers.CurrentAuthenticatedUser;
import com.example.demo.model.User;
import com.example.demo.model.enums.RoomType;
import com.example.demo.model.room.Room;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class VoiceRoomService {

  // serverId -> (roomId -> userIds)
  private final Map<Long, Map<Long, Set<Long>>> servers = new ConcurrentHashMap<>();

  // serverId -> sink that emits updates
  private final Map<Long, Sinks.Many<ServerVoiceRoomsDTO>> serverSinks = new ConcurrentHashMap<>();

  private final UserRepository userRepository;
  private final RoomRepository roomRepository;
  private final CurrentAuthenticatedUser currentAuthenticatedUser;

  public VoiceRoomService(UserRepository userRepository, RoomRepository roomRepository,
      CurrentAuthenticatedUser currentAuthenticatedUser) {
    this.userRepository = userRepository;
    this.roomRepository = roomRepository;
    this.currentAuthenticatedUser = currentAuthenticatedUser;
  }

  public Flux<ServerVoiceRoomsDTO> subscribeToServer(Long serverId, Long userId) {
    serverSinks.putIfAbsent(serverId, Sinks.many().replay().latest());
    servers.putIfAbsent(serverId, new ConcurrentHashMap<>());

    // Emit initial full state immediately
    serverSinks.get(serverId).tryEmitNext(buildServerDTO(serverId));

    return serverSinks.get(serverId)
        .asFlux()
        .doFinally(signalType -> {
          forceLeaveAllRooms(serverId, userId);
        });
  }

  public void joinRoom(Long serverId, Long roomId, Long userId) {
    servers.putIfAbsent(serverId, new ConcurrentHashMap<>());

    // Remove user from all other rooms in this server
    servers.get(serverId).values().forEach(set -> set.remove(userId));

    servers.get(serverId).putIfAbsent(roomId, ConcurrentHashMap.newKeySet());
    servers.get(serverId).get(roomId).add(userId);

    emitUpdate(serverId);
  }

  public void leaveRoom(Long serverId, Long roomId, Long userId) {
    if (!servers.containsKey(serverId))
      return;

    Map<Long, Set<Long>> roomMap = servers.get(serverId);

    Set<Long> users = roomMap.get(roomId);
    if (users != null) {
      users.remove(userId);
      emitUpdate(serverId);
    }
  }

  private void emitUpdate(Long serverId) {
    Sinks.Many<ServerVoiceRoomsDTO> sink = serverSinks.get(serverId);
    if (sink != null) {
      sink.tryEmitNext(buildServerDTO(serverId));
    }
  }

  // if refresh or close tab
  public void forceLeaveAllRooms(Long serverId, Long userId) {
    if (!servers.containsKey(serverId))
      return;

    servers.get(serverId).values().forEach(set -> set.remove(userId));
    emitUpdate(serverId);
  }

  private ServerVoiceRoomsDTO buildServerDTO(Long serverId) {
    List<Room> voiceRooms = roomRepository.findByServerIdAndType(serverId, RoomType.VOICE);

    Map<Long, Set<Long>> roomMap = servers.getOrDefault(serverId, new ConcurrentHashMap<>());

    List<RoomWithUsersDTO> rooms = voiceRooms.stream()
        .map(room -> {
          Set<Long> userIds = roomMap.getOrDefault(room.getId(), Collections.emptySet());

          List<SocketUserDTO> users = userIds.stream()
              .map(userId -> userRepository.findById(userId)
                  .map(SocketUserDTO::new)
                  .orElse(null))
              .filter(Objects::nonNull)
              .collect(Collectors.toList());

          return new RoomWithUsersDTO(room.getId(), room.getName(), users);
        })
        .collect(Collectors.toList());

    return new ServerVoiceRoomsDTO(serverId, rooms);
  }

}
