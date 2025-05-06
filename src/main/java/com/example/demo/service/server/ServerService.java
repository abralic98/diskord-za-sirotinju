// U SERVICE RADIMO LOGIKU
package com.example.demo.service.server;

import com.example.demo.config.EndpointProtector;
import com.example.demo.controller.inputs.server.CreateServerInput;
import com.example.demo.helpers.CurrentAuthenticatedUser;
import com.example.demo.model.User;
import com.example.demo.model.server.Server;
import com.example.demo.repository.ServerRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServerService {

  private final ServerRepository serverRepository;
  private final CurrentAuthenticatedUser currentAuthenticatedUser;

  public ServerService(ServerRepository serverRepository, CurrentAuthenticatedUser currentAuthenticatedUser) {
    this.serverRepository = serverRepository;
    this.currentAuthenticatedUser = currentAuthenticatedUser;
  }

  public Server createServer(CreateServerInput serverInput) {
    EndpointProtector.checkAuth();
    User user = currentAuthenticatedUser.getUser();
    Server newServer = new Server(serverInput.getName(), user, serverInput.getPublicServer());
    return serverRepository.save(newServer);
  }

  public List<Server> getAllServers() {
    EndpointProtector.checkAuth();
    User user = currentAuthenticatedUser.getUser();
    return serverRepository.findByJoinedUsersContaining(user);
  }

  // Get a user by their ID

}
