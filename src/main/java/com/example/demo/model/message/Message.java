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

  public Message(String text, MessageType type, Room room, User author) {
    this.text = text;
    this.type = type;
    this.room = room;
    this.author = author;
    this.dateCreated = new Date();
    this.dateUpdated = new Date();
  }

  @PrePersist // This method is called before the entity is persisted (inserted) into the
              // database, ensuring that dateCreated is set when a new user is created.
  protected void onCreate() {
    dateCreated = new Date();
    dateUpdated = new Date(); // Set dateUpdated to the current date when the entity is first created
  }

  @PreUpdate // This method is called before the entity is updated in the database, ensuring
             // that dateUpdated is updated whenever the entity is modified.
  protected void onUpdate() {
    dateUpdated = new Date(); // Update dateUpdated whenever the entity is updated
  }
  
}
