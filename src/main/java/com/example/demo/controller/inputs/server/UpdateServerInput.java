package com.example.demo.controller.inputs.server;

public class UpdateServerInput extends CreateServerInput {

  private Long id;
  private String banner;
  private String serverImg;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getBanner(){
    return banner;
  }

  public void setBanner(String banner){
    this.banner = banner;
  }

  public String getServerImg(){
    return serverImg;
  }

  public void setServerImg(String serverImg){
    this.serverImg = serverImg;
  }
}
