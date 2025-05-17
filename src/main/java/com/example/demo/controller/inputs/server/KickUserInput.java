package com.example.demo.controller.inputs.server;

public class KickUserInput {

  private Long userId;
  private Long serverId;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getServerId() {
    return serverId;
  }

  public void setServerId(Long serverId) {
    this.serverId = serverId;
  }

}
