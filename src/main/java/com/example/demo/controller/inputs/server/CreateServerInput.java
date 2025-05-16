
package com.example.demo.controller.inputs.server;

public class CreateServerInput {

  private String name;
  private String description;
  private Boolean publicServer;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getPublicServer() {
    return publicServer;
  }

  public void setPublicServer(Boolean publicServer) {
    this.publicServer = publicServer;
  }

}
