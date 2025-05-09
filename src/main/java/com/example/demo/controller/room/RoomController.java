package com.example.demo.controller.room;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.example.demo.controller.inputs.room.CreateRoomInput;
import com.example.demo.dto.rooms.Rooms;
import com.example.demo.model.room.Room;
import com.example.demo.service.room.RoomService;

@Controller
public class RoomController {

  private final RoomService roomService;

  public RoomController(RoomService roomService) {
    this.roomService = roomService;
  }

  @MutationMapping
  public Room createRoom(@Argument CreateRoomInput room) {
    return roomService.createRoom(room);
  }

  @QueryMapping
  public Rooms getRoomsByServerId(@Argument Long id) {
    return roomService.getRoomsByServerId(id);
  }

  @QueryMapping
  public Room getRoomById(@Argument Long id) {
    return roomService.getRoomById(id);
  }

}
