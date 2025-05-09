package com.example.demo.dto.rooms;

import java.util.List;

import com.example.demo.model.room.Room;

public class Rooms {
  private List<Room> text;
  private List<Room> voice;

  public Rooms() {
  }

  public Rooms(List<Room> text, List<Room> voice) {
    this.text = text;
    this.voice = voice;
  }

  public List<Room> getText() {
    return text;
  }

  public void setText(List<Room> text) {
    this.text = text;
  }

  public List<Room> getVoice() {
    return voice;
  }

  public void setVoice(List<Room> voice) {
    this.voice = voice;
  }

}
