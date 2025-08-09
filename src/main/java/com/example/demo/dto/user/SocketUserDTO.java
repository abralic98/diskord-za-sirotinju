package com.example.demo.dto.user;

import com.example.demo.model.User;

public class SocketUserDTO {
  private Long id;
  private String username;
  private String avatar;

  public SocketUserDTO(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.avatar = user.getAvatar();
  }

  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getAvatar() {
    return avatar;
  }
}
