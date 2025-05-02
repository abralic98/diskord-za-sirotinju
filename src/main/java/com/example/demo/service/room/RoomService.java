package com.example.demo.service.room;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.config.EndpointProtector;
import com.example.demo.controller.inputs.room.CreateRoomInput;
import com.example.demo.helpers.CurrentAuthenticatedUser;
import com.example.demo.model.User;
import com.example.demo.model.room.Room;
import com.example.demo.model.server.Server;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.ServerRepository;

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

  public Optional<Room> getRoomById(Long id) {
    EndpointProtector.checkAuth();
    try {
      Optional<Room> room = roomRepository.findById(id);
      return room;
    } catch (Exception e) {
      return null;
    }
  }

  public Room createRoom(CreateRoomInput input) {
    EndpointProtector.checkAuth();
    User user = currentAuthenticatedUser.getUser();
    Server server = serverRepository.getById(input.getServerId());

    Room room = new Room(input.getName(), user, server);
    return roomRepository.save(room);
  }

  public List<Room> getRoomsByServerId(Long id) {
    EndpointProtector.checkAuth();
    List<Room> rooms = roomRepository.findByServerId(id);
    return rooms;
  }
}
