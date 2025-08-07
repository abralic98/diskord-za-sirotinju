package com.example.demo.model.firebase;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.demo.model.User;

@Entity
@Table(name = "firebase_tokens")
public class Firebase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = true, unique = true)
  private String token;

  @ManyToMany(mappedBy = "firebaseTokens")
  private List<User> users = new ArrayList<>();

  public Firebase() {
  }

  public Firebase(String token, List<User> users) {
    this.token = token;
    this.users = users;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date dateCreated;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date dateUpdated;

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
