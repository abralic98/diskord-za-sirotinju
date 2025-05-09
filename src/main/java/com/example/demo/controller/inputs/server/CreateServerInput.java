
package com.example.demo.controller.inputs.server;

public class CreateServerInput {

  private String name;
  private Boolean publicServer;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getPublicServer() {
    return publicServer;
  }

  public void setPublicServer(Boolean publicServer) {
    this.publicServer = publicServer;
  }

}
