
package com.example.demo.model.invite;

import jakarta.persistence.*;
import java.util.Date;
import java.util.UUID;

import com.example.demo.model.server.Server;

@Entity
@Table(name = "invites")
public class ServerInvite {

  @Id
  private String token;

  @ManyToOne
  @JoinColumn(name = "server_id", nullable = false)
  private Server server;

  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;

  @Temporal(TemporalType.TIMESTAMP)
  private Date expiresAt;

  public ServerInvite() {
  }

  public ServerInvite(Server server, long validityDurationInMinutes) {
    this.token = UUID.randomUUID().toString();
    this.server = server;
    this.createdAt = new Date();
    this.expiresAt = new Date(System.currentTimeMillis() + validityDurationInMinutes * 60 * 1000);
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Server getServer() {
    return server;
  }

  public void setServer(Server server) {
    this.server = server;
  }

  public Date getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(Date expiresAt) {
    this.expiresAt = expiresAt;
  }
}
