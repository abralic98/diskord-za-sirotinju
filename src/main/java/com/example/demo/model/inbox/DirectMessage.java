package com.example.demo.model.inbox;

import java.util.Date;

import com.example.demo.model.User;
import com.example.demo.model.enums.MessageType;

import jakarta.persistence.*;

@Entity
public class DirectMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String text;

  @Column(nullable = false)
  private MessageType type;

  @Column(nullable = true)
  private String imageUrl;

  @ManyToOne
  @JoinColumn(name = "inbox_id", nullable = false)
  private Inbox inbox;

  @ManyToOne
  @JoinColumn(name = "author", nullable = false)
  private User author;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date dateCreated;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date dateUpdated;

  public DirectMessage() {

  }

  public DirectMessage(String text, MessageType type, String imageUrl, Inbox inbox, User author) {
    this.text = text;
    this.type = type;
    this.inbox = inbox;
    this.imageUrl = imageUrl;
    this.author = author;
    this.dateCreated = new Date();
    this.dateUpdated = new Date();
  }

  public Inbox getInbox() {
    return inbox;
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
