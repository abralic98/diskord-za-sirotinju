
package com.example.demo.controller.inputs.inbox;

import com.example.demo.model.enums.MessageType;

public class CreateDMInput {

  private String text;
  private MessageType type;
  private Long inboxId;

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

  public Long getInboxId() {
    return inboxId;
  }

  public void setInboxId(Long inboxId) {
    this.inboxId = inboxId;
  }

}
