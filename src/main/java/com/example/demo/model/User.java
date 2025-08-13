
package com.example.demo.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.demo.model.enums.UserPresenceType;
import com.example.demo.model.firebase.Firebase;
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
  private String description;

  @Column(nullable = true)
  private Long phoneNumber;

  @Column(nullable = false)
  private Boolean active;

  @Column(nullable = true)
  private String avatar;

  @Column(nullable = true)
  private String banner;

  @Column(nullable = true)
  private UserPresenceType userPresence;

  @ManyToMany(mappedBy = "joinedUsers")
  private List<Server> joinedServers = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BannedUser> bannedInServers = new ArrayList<>();

  @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(name = "user_firebase_tokens", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "firebase_token_id"))
  private List<Firebase> firebaseTokens = new ArrayList<>();

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date dateCreated;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date dateUpdated;

  public User() {
  }

  public User(String username, String password, String email) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.active = false;
    this.userPresence = UserPresenceType.OFFLINE;
  }

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

  public String getBanner() {
    return banner;
  }

  public void setBanner(String banner) {
    this.banner = banner;
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

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
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

  public List<Firebase> getFirebaseTokens() {
    return firebaseTokens;
  }

  public void setFirebaseTokens(List<Firebase> firebaseTokens) {
    this.firebaseTokens = firebaseTokens;
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
