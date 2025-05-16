// U SERVICE RADIMO LOGIKU
package com.example.demo.service.server;

import com.example.demo.config.EndpointProtector;
import com.example.demo.controller.inputs.server.CreateServerInput;
import com.example.demo.controller.inputs.server.JoinServerInput;
import com.example.demo.controller.inputs.server.UpdateServerInput;
import com.example.demo.dto.server.ServerPageDTO;
import com.example.demo.helpers.CurrentAuthenticatedUser;
import com.example.demo.model.User;
import com.example.demo.model.server.Server;
import com.example.demo.repository.ServerRepository;
import com.example.demo.controller.global.ModifiedException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    Server newServer = new Server(serverInput.getName(), user, serverInput.getPublicServer(),
        serverInput.getDescription());
    return serverRepository.save(newServer);
  }

  public Server updateServer(UpdateServerInput serverInput) {
    EndpointProtector.checkAuth();
    User user = currentAuthenticatedUser.getUser();
    Server server = serverRepository.findById(serverInput.getId()).orElseThrow(()-> new ModifiedException("Server not found"));

    if (serverInput.getName() != null) {
      server.setName(serverInput.getName());
    }

    if (serverInput.getDescription() != null) {
      server.setDescription(serverInput.getDescription());
    }

    if (serverInput.getPublicServer() != null) {
      server.setIsPublicServer(serverInput.getPublicServer());
    }

    if (serverInput.getBanner() != null) {
      server.setBanner(serverInput.getBanner());
    }

    if (serverInput.getServerImg() != null) {
      server.setServerImg(serverInput.getServerImg());
    }

    return serverRepository.save(server);
  }

  public List<Server> getAllUserServers() {
    EndpointProtector.checkAuth();
    User user = currentAuthenticatedUser.getUser();
    return serverRepository.findByJoinedUsersContaining(user);
  }

  public ServerPageDTO getAllServers(int page, int size) {
    EndpointProtector.checkAuth();
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "name"));
    Page<Server> serverPage = serverRepository.findAll(pageable);
    return new ServerPageDTO(serverPage);
  }

  public Server getServerById(Long id) {
    EndpointProtector.checkAuth();
    User user = currentAuthenticatedUser.getUser();

    Server server = serverRepository.findById(id)
        .orElseThrow(() -> new ModifiedException("Server not found"));

    if (!server.getJoinedUsers().contains(user)) {
      throw new ModifiedException("Access denied: user has not joined this server");
    }

    return server;
  }

  public Server joinServer(JoinServerInput input) {
    EndpointProtector.checkAuth();

    System.out.println(input.getId());
    User user = currentAuthenticatedUser.getUser();
    Server server = serverRepository.findById(input.getId())
        .orElseThrow(() -> new ModifiedException("Server not found"));

    if (server.getJoinedUsers().contains(user)) {
      throw new ModifiedException("User has already joined to this server");
    }

    server.getJoinedUsers().add(user);
    serverRepository.save(server);
    return server;
  }

}
