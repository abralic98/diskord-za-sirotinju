package com.example.demo.service.room;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.config.EndpointProtector;
import com.example.demo.controller.global.ModifiedException;
import com.example.demo.controller.inputs.room.CreateRoomInput;
import com.example.demo.helpers.CurrentAuthenticatedUser;
import com.example.demo.model.User;
import com.example.demo.model.enums.RoomType;
import com.example.demo.model.room.Room;
import com.example.demo.model.server.Server;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.ServerRepository;
import com.example.demo.dto.rooms.Rooms;

@Service
public class RoomService {

  private final RoomRepository roomRepository;
  private final ServerRepository serverRepository;
  private final CurrentAuthenticatedUser currentAuthenticatedUser;

  public RoomService(RoomRepository roomRepository, ServerRepository serverRepository,
      CurrentAuthenticatedUser currentAuthenticatedUser) {
    this.roomRepository = roomRepository;
    this.currentAuthenticatedUser = currentAuthenticatedUser;
    this.serverRepository = serverRepository;
  }

  public Room getRoomById(Long id) {
    EndpointProtector.checkAuth();
    User user = currentAuthenticatedUser.getUser();

    Room room = roomRepository.findById(id).orElseThrow(() -> new ModifiedException(("Room not found")));
    Server server = room.getServer();

    if (!server.getJoinedUsers().contains(user)) {
      throw new ModifiedException("Access denied: user has not joined this server");
    }

    return room;
  }

  public Room createRoom(CreateRoomInput input) {
    EndpointProtector.checkAuth();
    User user = currentAuthenticatedUser.getUser();
    Server server = serverRepository.getById(input.getServerId());

    Room room = new Room(input.getName(), user, server, input.getType());
    return roomRepository.save(room);
  }

  // public List<Room> getRoomsByServerId(Long id) {
  // EndpointProtector.checkAuth();
  // User user = currentAuthenticatedUser.getUser();
  // Server server = serverRepository.findById(id).orElseThrow(() -> new
  // ModifiedException("Server not found"));
  // if (!server.getJoinedUsers().contains(user)) {
  // throw new ModifiedException("Access denied: user has not joined this
  // server");
  // }
  // List<Room> rooms = roomRepository.findByServerId(id);
  // return rooms;
  // }
  public Rooms getRoomsByServerId(Long id) {
    EndpointProtector.checkAuth();
    User user = currentAuthenticatedUser.getUser();
    Server server = serverRepository.findById(id)
        .orElseThrow(() -> new ModifiedException("Server not found"));

    if (!server.getJoinedUsers().contains(user)) {
      throw new ModifiedException("Access denied: user has not joined this server");
    }

    List<Room> rooms = roomRepository.findByServerId(id);
    Rooms result = new Rooms();
    result.setText(new ArrayList<>());
    result.setVoice(new ArrayList<>());

    for (Room room : rooms) {
      if (room.getType().equals(RoomType.TEXT)) {
        result.getText().add(room);
      } else if (room.getType().equals(RoomType.VOICE)) {
        result.getVoice().add(room);
      }
    }

    return result;
  }
}
