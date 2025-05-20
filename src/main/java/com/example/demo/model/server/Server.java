package com.example.demo.model.server;

import jakarta.persistence.*;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.demo.model.User;
import com.example.demo.model.room.Room;
import com.example.demo.model.user.BannedUser;

@Entity
@Table(name = "servers")
public class Server {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = true)
  private String description;

  @Column(nullable = true)
  private String banner;

  @Column(nullable = true)
  private String serverImg;

  @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Room> rooms;

  @Column(nullable = false)
  private Boolean publicServer;

  @ManyToOne
  @JoinColumn(name = "created_by", nullable = false)
  private User createdBy;

  @ManyToMany
  @JoinTable(name = "server_users", joinColumns = @JoinColumn(name = "server_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
  private List<User> joinedUsers = new ArrayList<>();

  @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BannedUser> bannedUsers = new ArrayList<>();

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date dateCreated;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date dateUpdated;

  // OVO STAVI DEFAULT KONSTRUKOTR JPA LIBRARY
  public Server() {

  }

  public Server(String name, User createdBy, Boolean publicServer, String description) {
    this.name = name;
    this.description = description;
    this.createdBy = createdBy;
    this.publicServer = publicServer;
    this.joinedUsers = new ArrayList<>();
    this.joinedUsers.add(createdBy);
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getBanner() {
    return banner;
  }

  public void setBanner(String banner) {
    this.banner = banner;
  }

  public String getServerImg() {
    return serverImg;
  }

  public void setServerImg(String serverImg) {
    this.serverImg = serverImg;
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

  public List<User> getJoinedUsers() {
    return joinedUsers;
  }

  public void setJoinedUsers(List<User> joinedUsers) {
    this.joinedUsers = joinedUsers;
  }

  public List<BannedUser> getBannedUsers() {
    return bannedUsers;
  }

  public void setBannedUsers(List<BannedUser> bannedUsers) {
    this.bannedUsers = bannedUsers;
  }

  public boolean getIsPublicServer() {
    return publicServer;
  }

  public void setIsPublicServer(Boolean isPublic) {
    this.publicServer = isPublic;
  }
  @PreRemove
  private void removeUsersFromServer() {
      joinedUsers.clear();
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
