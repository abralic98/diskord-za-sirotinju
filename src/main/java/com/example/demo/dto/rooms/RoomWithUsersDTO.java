
package com.example.demo.dto.rooms;

import java.util.List;
import com.example.demo.dto.user.SocketUserDTO;

public class RoomWithUsersDTO {
  private Long roomId;
  private String name;
  private List<SocketUserDTO> users;

  public RoomWithUsersDTO(Long roomId, String name, List<SocketUserDTO> users) {
    this.roomId = roomId;
    this.name = name;
    this.users = users;
  }

  public Long getRoomId() {
    return roomId;
  }

  public String getName() {
    return name;
  }

  public List<SocketUserDTO> getUsers() {
    return users;
  }
}
