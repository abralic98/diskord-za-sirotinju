
package com.example.demo.model.inbox;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.demo.model.User;

import jakarta.persistence.*;

@Entity
@Table(name = "inbox")
public class Inbox {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(mappedBy = "inbox", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DirectMessage> messages;

  @ManyToMany
  @JoinTable(name = "inbox_users", joinColumns = @JoinColumn(name = "inbox_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
  private List<User> users = new ArrayList<>();

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date dateCreated;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date dateUpdated;

  public Inbox() {
  }

  public Inbox(User createdBy, User joinedUser) {
    this.users = new ArrayList<>();
    this.users.add(createdBy);
    this.users.add(joinedUser);
    this.dateCreated = new Date();
    this.dateUpdated = new Date();
  }

  public Long getId() {
    return id;
  }

  public List<DirectMessage> getMessages() {
    return messages;
  }

  public void setMessages(List<DirectMessage> messages) {
    this.messages = messages;
  }

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }

  public Date getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(Date date) {
    this.dateCreated = date;
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
