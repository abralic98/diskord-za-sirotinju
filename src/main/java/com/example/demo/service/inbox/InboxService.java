
package com.example.demo.service.inbox;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.config.EndpointProtector;
import com.example.demo.controller.global.ModifiedException;
import com.example.demo.helpers.CurrentAuthenticatedUser;
import com.example.demo.model.User;
import com.example.demo.model.inbox.Inbox;
import com.example.demo.repository.InboxRepository;
import com.example.demo.repository.UserRepository;

@Service
public class InboxService {

  private final InboxRepository inboxRepository;
  private final CurrentAuthenticatedUser currentAuthenticatedUser;
  private final UserRepository userRepository;

  public InboxService(InboxRepository inboxRepository, CurrentAuthenticatedUser currentAuthenticatedUser,
      UserRepository userRepository) {
    this.inboxRepository = inboxRepository;
    this.currentAuthenticatedUser = currentAuthenticatedUser;
    this.userRepository = userRepository;
  }

  public Inbox createInbox(Long withUserId) {
    EndpointProtector.checkAuth();
    User currentUser = currentAuthenticatedUser.getUser();
    User joinedUser = userRepository.findById(withUserId)
        .orElseThrow(() -> new ModifiedException("Joined user not found"));

    if (currentUser.getId().equals(joinedUser.getId())) {
      throw new ModifiedException("You cannot create DM with yourself");
    }

    Optional<Inbox> existingInbox = inboxRepository.findDirectInboxBetween(currentUser, joinedUser);

    if (existingInbox.isPresent()) {
      return existingInbox.get();
    }

    Inbox inbox = new Inbox(currentUser, joinedUser);
    return inboxRepository.save(inbox);
  }

  public Boolean removeMeFromInbox(Long inboxId) {
    EndpointProtector.checkAuth();
    User currentUser = currentAuthenticatedUser.getUser();

    Inbox inbox = inboxRepository.findById(inboxId)
        .orElseThrow(() -> new ModifiedException("Inbox not found"));

    inbox.getUsers().removeIf(user -> user.getId().equals(currentUser.getId()));

    inboxRepository.save(inbox);

    if (inbox.getUsers().isEmpty()) {
      inboxRepository.delete(inbox);
    }
    return true;
  }

  public Inbox addUserToInbox(Long inboxId, Long userId) {
    EndpointProtector.checkAuth();
    User currentUser = currentAuthenticatedUser.getUser();

    Inbox inbox = inboxRepository.findById(inboxId)
        .orElseThrow(() -> new ModifiedException("Inbox not found"));

    User userToAdd = userRepository.findById(userId).orElseThrow(() -> new ModifiedException("User not found"));

    if (!inbox.getUsers().contains(currentUser)) {
      throw new ModifiedException("You dont have permission to add user");
    }
    if (inbox.getUsers().contains(userToAdd)) {
      throw new ModifiedException("User is already in this inbox");
    }

    inbox.getUsers().add(userToAdd);
    return inboxRepository.save(inbox);
  }

  public List<Inbox> getMyInbox() {
    EndpointProtector.checkAuth();
    User currentUser = currentAuthenticatedUser.getUser();
    return inboxRepository.findByUsers_Id(currentUser.getId());
  }

  public Inbox getInboxById(Long id) {
    EndpointProtector.checkAuth();
    Inbox inbox = inboxRepository.findById(id).orElseThrow(() -> new ModifiedException("No inbox with that id"));
    return inbox;
  }

}
