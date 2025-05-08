
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

  @OneToMany(mappedBy = "room")
  private List<Message> messages;

  // Many rooms belong to one server
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
