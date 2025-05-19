package com.example.demo.controller.server;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.example.demo.controller.inputs.server.BanUserInput;
import com.example.demo.controller.inputs.server.CreateServerInput;
import com.example.demo.controller.inputs.server.JoinServerInput;
import com.example.demo.controller.inputs.server.KickUserInput;
import com.example.demo.controller.inputs.server.UnbanUserInput;
import com.example.demo.controller.inputs.server.UpdateServerInput;
import com.example.demo.dto.server.ServerPageDTO;
import com.example.demo.model.User;
import com.example.demo.model.server.Server;
import com.example.demo.model.user.BannedUser;
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

  @MutationMapping
  public Server updateServer(@Argument UpdateServerInput server) {
    return serverService.updateServer(server);
  }

  @QueryMapping
  public List<Server> getAllUserServers() {
    return serverService.getAllUserServers();
  }

  @QueryMapping
  public ServerPageDTO getAllServers(@Argument int page, @Argument int size) {
    return serverService.getAllServers(page, size);
  }

  @QueryMapping
  public Server getServerById(@Argument Long id) {
    return serverService.getServerById(id);
  }

  @MutationMapping
  public Server joinServer(@Argument JoinServerInput input) {
    return serverService.joinServer(input);
  }

  @MutationMapping
  public Boolean kickUserFromServer(@Argument KickUserInput input ) {
    return serverService.kickUserFromServer(input);
  }

  @MutationMapping
  public Boolean banUserFromServer(@Argument BanUserInput input ) {
    return serverService.banUserFromServer(input);
  }

  @MutationMapping
  public Boolean unbanUserFromServer(@Argument UnbanUserInput input ) {
    return serverService.unbanUserFromServer(input);
  }

  @QueryMapping
  public List<BannedUser> getBannedUsersByServerId(@Argument Long id) {
    return serverService.getBannedUsersByServerId(id);
  }

}
