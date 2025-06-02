
package com.example.demo.model.room;

import java.util.Date;
import java.util.List;

import com.example.demo.model.User;
import com.example.demo.model.enums.RoomType;
import com.example.demo.model.message.Message;
import com.example.demo.model.server.Server;

import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private RoomType type;

  @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Message> messages;

  @ManyToOne
  @JoinColumn(name = "server_id", nullable = false)
  private Server server;

  @ManyToOne
  @JoinColumn(name = "created_by", nullable = false)
  private User createdBy;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date dateCreated;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date dateUpdated;

  public Room() {

  }

  public Room(String name, User user, Server server, RoomType type) {
    this.name = name;
    this.server = server;
    this.createdBy = user;
    this.type = type;
    this.dateCreated = new Date();
    this.dateUpdated = new Date();
  }

  public Long getId() {
    return id;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }

  public Server getServer() {
    return server;
  }

  public RoomType getType() {
    return type;
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
