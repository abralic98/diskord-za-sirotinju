package com.example.demo.model.server;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.List;

import com.example.demo.model.User;
import com.example.demo.model.room.Room;

@Entity
@Table(name = "servers")
public class Server {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  // One server has many rooms
  @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Room> rooms;

  // Many servers can be created by one user
  @ManyToOne
  @JoinColumn(name = "created_by", nullable = false)
  private User createdBy;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date dateCreated;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date dateUpdated;

  // OVO STAVI DEFAULT KONSTRUKOTR JPA LIBRARY
  public Server() {

  }

  public Server(String name, User createdBy) {
    this.name = name;
    this.createdBy = createdBy;
    this.dateCreated = new Date();
    this.dateUpdated = new Date();
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(Date dateCreated) {
    this.dateCreated = dateCreated;
  }

  public Date getDateUpdated() {
    return dateUpdated;
  }

  public void setDateUpdated(Date dateUpdated) {
    this.dateUpdated = dateUpdated;
  }

  public List<Room> getRooms() {
    return rooms;
  }

  public void setRooms(List<Room> rooms) {
    this.rooms = rooms;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
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
