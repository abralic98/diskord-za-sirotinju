
package com.example.demo.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.demo.model.enums.UserPresenceType;
import com.example.demo.model.server.Server;
import com.example.demo.model.user.BannedUser;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = true)
  private Long phoneNumber;

  @Column(nullable = false)
  private Boolean active;

  @Column(nullable = true)
  private String avatar;

  @Column(nullable = true)
  private UserPresenceType userPresence;

  @ManyToMany(mappedBy = "joinedUsers")
  private List<Server> joinedServers = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BannedUser> bannedInServers = new ArrayList<>();

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date dateCreated;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date dateUpdated;

  // Default constructor
  public User() {
  }

  // Constructor to set fields when creating a User object
  public User(String username, String password, String email) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.active = false;
    this.userPresence = UserPresenceType.OFFLINE;
  }

  // Getters and setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public Boolean getIsUserActive() {
    return active;
  }

  public void setActivateUser(Boolean active) {
    this.active = active;
  }

  public Date getDateCreated() {
    return dateCreated;
  }

  public void setPhoneNumber(Long phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public Number getPhoneNumber() {
    return phoneNumber;
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

  public UserPresenceType getUserPresence() {
    return userPresence;
  }

  public void setUserPresence(UserPresenceType userPresenceType) {
    this.userPresence = userPresenceType;
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
