package com.example.demo.dto.server;

import java.util.List;

import com.example.demo.dto.rooms.RoomWithUsersDTO;

public class ServerVoiceRoomsDTO {
  private Long serverId;
  private List<RoomWithUsersDTO> rooms;

  public ServerVoiceRoomsDTO(Long serverId, List<RoomWithUsersDTO> rooms) {
    this.serverId = serverId;
    this.rooms = rooms;
  }

  public Long getServerId() {
    return serverId;
  }

  public List<RoomWithUsersDTO> getRooms() {
    return rooms;
  }
}
