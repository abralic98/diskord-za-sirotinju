package com.example.demo.model.message;

import java.util.Date;

import com.example.demo.model.User;
import com.example.demo.model.enums.MessageType;
import com.example.demo.model.room.Room;

import jakarta.persistence.*;

@Entity
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String text;

  @Column(nullable = false)
  private MessageType type;

  @Column(nullable = true)
  private String imageUrl;

  @ManyToOne
  @JoinColumn(name = "room_id", nullable = false)
  private Room room;

  @ManyToOne
  @JoinColumn(name = "author", nullable = false)
  private User author;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date dateCreated;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date dateUpdated;

  public Message() {

  }

  public Message(String text, MessageType type, String imageUrl, Room room, User author) {
    this.text = text;
    this.type = type;
    this.imageUrl = imageUrl;
    this.room = room;
    this.author = author;
    this.dateCreated = new Date();
    this.dateUpdated = new Date();
  }

  public Room getRoom() {
    return room;
  }

  @PrePersist 
  protected void onCreate() {
    dateCreated = new Date();
    dateUpdated = new Date(); 
  }

  @PreUpdate
  protected void onUpdate() {
    dateUpdated = new Date();
  }

}
