package com.example.demo.controller.server;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.example.demo.controller.inputs.server.CreateServerInput;
import com.example.demo.model.server.Server;
import com.example.demo.service.server.ServerService;

import java.util.List;

@Controller 
public class ServerController {

  private final ServerService serverService;

  public ServerController(ServerService serverService) {
    this.serverService = serverService;
  }

  @MutationMapping
  public Server createServer(@Argument CreateServerInput server) {
    return serverService.createServer(server); 
  }

  @QueryMapping
  public List<Server> getAllServers() {
    return serverService.getAllServers(); 
  }
  // GraphQL query for getting a user by their ID
  // @QueryMapping
  // public Optional<User> getServerById(@Argument Long id) {
  //   return userService.getUserById(id); // Fetch user by ID from the service
  // }
  //
  //
  // @MutationMapping
  // public User deactivateUser(@Argument Long id) {
  //   return userService.deactivateUser(id); // Create a user via the service
  // }

}
