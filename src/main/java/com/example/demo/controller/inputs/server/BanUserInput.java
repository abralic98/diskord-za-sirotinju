
package com.example.demo.controller.inputs.server;

public class BanUserInput extends KickUserInput {

  private String reason;

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }
}
