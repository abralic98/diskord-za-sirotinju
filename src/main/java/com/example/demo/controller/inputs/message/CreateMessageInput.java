package com.example.demo.controller.inputs.message;

import com.example.demo.model.enums.MessageType;

public class CreateMessageInput {

  private String text;
  private MessageType type;
  private Long roomId;

  public String getText() {
    return text;
  }

  public void setText(String name) {
    this.text = name;
  }

  public MessageType getType() {
    return type;
  }

  public void setType(MessageType type) {
    this.type = type;
  }

  public Long getRoomId() {
    return roomId;
  }

  public void setRoomId(Long roomId) {
    this.roomId = roomId;
  }

}
