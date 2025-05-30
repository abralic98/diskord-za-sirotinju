package com.example.demo.controller.inputs.user;

import com.example.demo.model.enums.UserPresenceType;

public class UpdateUserInput extends CreateUserInput {
  private String description;
  private Long phoneNumber;
  private String avatar;
  private UserPresenceType userPresence;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(Long phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public UserPresenceType getUserPresence() {
    return userPresence;
  }

  public void setUserPresence(UserPresenceType userPresence) {
    this.userPresence = userPresence;
  }

}
