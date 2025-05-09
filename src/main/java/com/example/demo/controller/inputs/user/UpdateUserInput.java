package com.example.demo.controller.inputs.user;

public class UpdateUserInput {
  private String username;
  private String password;
  private String email;
  private Number phoneNumber;

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

  public Number getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(Number phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

}
