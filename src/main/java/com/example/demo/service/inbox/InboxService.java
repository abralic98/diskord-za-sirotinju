
package com.example.demo.service.inbox;

import java.util.List;

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
        .orElseThrow(() -> new ModifiedException("joined user not found"));
    Inbox inbox = new Inbox(currentUser, joinedUser);

    return inboxRepository.save(inbox);
  }

  public List<Inbox> getMyInbox() {
    EndpointProtector.checkAuth();
    User currentUser = currentAuthenticatedUser.getUser();
    return inboxRepository.findByUsers_Id(currentUser.getId());
  }

  public Inbox getInboxById(Long id) {
    EndpointProtector.checkAuth();
    return inboxRepository.getById(id);
  }

}
