package com.example.demo.controller.inputs.room;

import com.example.demo.model.enums.RoomType;

public class CreateRoomInput {

  private String name;
  private RoomType type;
  private Long serverId;

  // Corrected Getter and Setter
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public RoomType getType() {
    return type;
  }

  public void setType(RoomType type) {
    this.type = type;
  }

  public Long getServerId() {
    return serverId;
  }

  public void setServerId(Long serverId) {
    this.serverId = serverId;
  }

}
