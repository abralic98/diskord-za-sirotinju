// U SERVICE RADIMO LOGIKU
package com.example.demo.service.server;

import com.example.demo.config.EndpointProtector;
import com.example.demo.controller.inputs.server.BanUserInput;
import com.example.demo.controller.inputs.server.CreateServerInput;
import com.example.demo.controller.inputs.server.JoinServerInput;
import com.example.demo.controller.inputs.server.KickUserInput;
import com.example.demo.controller.inputs.server.UnbanUserInput;
import com.example.demo.controller.inputs.server.UpdateServerInput;
import com.example.demo.dto.server.ServerPageDTO;
import com.example.demo.helpers.CurrentAuthenticatedUser;
import com.example.demo.model.User;
import com.example.demo.model.invite.ServerInvite;
import com.example.demo.model.server.Server;
import com.example.demo.model.user.BannedUser;
import com.example.demo.repository.ServerInviteRepository;
import com.example.demo.repository.ServerRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.controller.global.ModifiedException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ServerService {

  private final ServerRepository serverRepository;
  private final CurrentAuthenticatedUser currentAuthenticatedUser;
  private final UserRepository userRepository;
  private final ServerInviteRepository serverInviteRepository;

  public ServerService(ServerRepository serverRepository, CurrentAuthenticatedUser currentAuthenticatedUser,
      UserRepository userRepository, ServerInviteRepository serverInviteRepository) {
    this.serverRepository = serverRepository;
    this.currentAuthenticatedUser = currentAuthenticatedUser;
    this.userRepository = userRepository;
    this.serverInviteRepository = serverInviteRepository;
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
    Server server = serverRepository.findById(serverInput.getId())
        .orElseThrow(() -> new ModifiedException("Server not found"));
    User user = currentAuthenticatedUser.getUser();
    if (server.getCreatedBy() != user) {
      throw new ModifiedException("Only server creator can edit server");
    }

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
    return serverRepository.findServersByUserOrderedByName(user);
  }

  public ServerPageDTO getAllServers(int page, int size, String search) {
    EndpointProtector.checkAuth();
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "name"));

    Page<Server> serverPage;

    if (search != null && !search.trim().isEmpty()) {
      serverPage = serverRepository.findByPublicServerTrueAndNameContainingIgnoreCase(search.trim(), pageable);
    } else {
      serverPage = serverRepository.findByPublicServerTrue(pageable);
    }

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

  public Boolean kickUserFromServer(KickUserInput input) {
    EndpointProtector.checkAuth();

    User user = currentAuthenticatedUser.getUser();
    // ako posaljes random string kao id npr lalalala on pukne ne znam to resolvat
    Server server = serverRepository.findById(input.getServerId())
        .orElseThrow(() -> new ModifiedException("Server not found"));

    Boolean permissionCheck = server.getCreatedBy().equals(user);

    if (!permissionCheck) {
      throw new ModifiedException("You dont have permission to ban");
    }

    User userToBeKicked = userRepository.findById(input.getUserId())
        .orElseThrow(() -> new ModifiedException("User not found"));

    if (!server.getJoinedUsers().contains(userToBeKicked)) {
      throw new ModifiedException("User cannot be kicked since its not on the server");
    }

    server.getJoinedUsers().remove(userToBeKicked);
    serverRepository.save(server);
    return true;
  }

  public Boolean banUserFromServer(BanUserInput input) {
    EndpointProtector.checkAuth();

    User user = currentAuthenticatedUser.getUser();
    Server server = serverRepository.findById(input.getServerId())
        .orElseThrow(() -> new ModifiedException("Server not found"));

    Boolean permissionCheck = server.getCreatedBy().equals(user);

    if (!permissionCheck) {
      throw new ModifiedException("You dont have permission to ban");
    }

    User userToBeBanned = userRepository.findById(input.getUserId())
        .orElseThrow(() -> new ModifiedException("User not found"));

    if (!server.getJoinedUsers().contains(userToBeBanned)) {
      throw new ModifiedException("User cannot be banned since its not on the server");
    }

    server.getJoinedUsers().remove(userToBeBanned);
    BannedUser banned = new BannedUser(userToBeBanned, server, input.getReason(), user);
    server.getBannedUsers().add(banned);
    serverRepository.save(server);
    return true;
  }

  public BannedUser getBannedUserByUserId(Server server, Long userId) {
    for (BannedUser bannedUser : server.getBannedUsers()) {
      if (bannedUser.getUser().getId().equals(userId)) {
        return bannedUser;
      }
    }
    throw new ModifiedException("User not found");
  }

  public Boolean unbanUserFromServer(UnbanUserInput input) {
    EndpointProtector.checkAuth();

    User user = currentAuthenticatedUser.getUser();
    Server server = serverRepository.findById(input.getServerId())
        .orElseThrow(() -> new ModifiedException("Server not found"));

    Boolean permissionCheck = server.getCreatedBy().equals(user);

    if (!permissionCheck) {
      throw new ModifiedException("You dont have permission to ban");
    }

    BannedUser bannedUser = getBannedUserByUserId(server, input.getUserId());

    System.out.println("loleoeawodewa");
    System.out.println(bannedUser);

    if (!server.getBannedUsers().contains(bannedUser)) {
      throw new ModifiedException("User is allready unbanned");
    }

    server.getBannedUsers().remove(bannedUser);

    User addUserToServer = userRepository.findById(input.getUserId())
        .orElseThrow(() -> new ModifiedException("User not found"));

    server.getJoinedUsers().add(addUserToServer);
    serverRepository.save(server);
    return true;
  }

  public List<BannedUser> getBannedUsersByServerId(Long id) {
    EndpointProtector.checkAuth();

    Server server = serverRepository.findById(id)
        .orElseThrow(() -> new ModifiedException("Server not found"));

    return server.getBannedUsers();
  }

  public Boolean deleteServer(Long id) {
    EndpointProtector.checkAuth();
    User user = currentAuthenticatedUser.getUser();
    Server server = serverRepository.findById(id).orElseThrow(() -> new ModifiedException("Server not found"));

    Boolean permissionCheck = server.getCreatedBy().equals(user);

    if (!permissionCheck) {
      throw new ModifiedException("You dont have permission to delete server");
    }
    serverRepository.delete(server);
    return true;
  }

  public String generateInviteLink(Long serverId) {
    EndpointProtector.checkAuth();
    User user = currentAuthenticatedUser.getUser();
    Server server = serverRepository.findById(serverId)
        .orElseThrow(() -> new RuntimeException("Server not found"));

    Boolean permissionCheck = server.getCreatedBy().equals(user);

    if (!permissionCheck) {
      throw new ModifiedException("You dont have permission to generate server link");
    }

    ServerInvite invite = new ServerInvite(server, 60);
    serverInviteRepository.save(invite);

    return "http://localhost:3000/invite/" + invite.getToken();
  }

  public Server joinServerWithInvite(String token) {
    EndpointProtector.checkAuth();
    User user = currentAuthenticatedUser.getUser();
    ServerInvite invite = serverInviteRepository.findByToken(token)
        .orElseThrow(() -> new ModifiedException("Invalid invite"));

    if (invite.getExpiresAt().before(new Date())) {
      throw new ModifiedException("Invite expired");
    }

    Server server = invite.getServer();
    if (server.getJoinedUsers().contains(user)) {
      throw new ModifiedException("User has already joined to this server");
    }
    if (!server.getJoinedUsers().contains(user)) {
      server.getJoinedUsers().add(user);
      serverRepository.save(server);
    }
    return server;
  }

  public Server getServerByInviteToken(String token){
    EndpointProtector.checkAuth();
    ServerInvite invite = serverInviteRepository.findByToken(token)
        .orElseThrow(() -> new RuntimeException("Invalid invite"));
    Server server = invite.getServer();
    return server;

  }
}
