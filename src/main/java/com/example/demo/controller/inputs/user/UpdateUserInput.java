package com.example.demo.controller.inputs.user;

import com.example.demo.model.enums.UserPresenceType;

public class UpdateUserInput {
  private String username;
  private String email;
  private Long phoneNumber;
  private String avatar;
  private UserPresenceType userPresence;

  // Getters and setters
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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
