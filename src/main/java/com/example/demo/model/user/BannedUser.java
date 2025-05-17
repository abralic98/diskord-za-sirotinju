package com.example.demo.model.user;

import java.util.Date;

import com.example.demo.model.User;
import com.example.demo.model.server.Server;

import jakarta.persistence.*;

@Entity
@Table(name = "banned_users")
public class BannedUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "server_id", nullable = false, unique = true)
  private Server server;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @ManyToOne
  @JoinColumn(name = "ban_author", nullable = false)
  private User banAuthor;

  private String reason;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date dateCreated;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date dateUpdated;

  public BannedUser() {
  }

  public BannedUser(User user, Server server, String reason, User banAuthor) {
    this.user = user;
    this.server = server;
    this.reason = reason;
    this.banAuthor = banAuthor;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Server getServer() {
    return server;
  }

  public void setServer(Server server) {
    this.server = server;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public User getBanAuthor() {
    return banAuthor;
  }

  public void setBanAuthor(User banAuthor) {
    this.banAuthor = banAuthor;
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
