
package com.example.demo.dto.rooms;

import java.util.List;
import com.example.demo.dto.user.SocketUserDTO;

public class RoomWithUsersDTO {
  private Long id;
  private String name;
  private List<SocketUserDTO> users;

  public RoomWithUsersDTO(Long id, String name, List<SocketUserDTO> users) {
    this.id = id;
    this.name = name;
    this.users = users;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public List<SocketUserDTO> getUsers() {
    return users;
  }
}
