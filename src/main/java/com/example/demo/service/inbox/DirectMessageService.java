package com.example.demo.service.inbox;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.config.EndpointProtector;
import com.example.demo.controller.global.ModifiedException;
import com.example.demo.controller.inputs.inbox.CreateDMInput;
import com.example.demo.dto.inbox.DirectMessagePageDTO;
import com.example.demo.helpers.CurrentAuthenticatedUser;
import com.example.demo.model.User;
import com.example.demo.model.inbox.DirectMessage;
import com.example.demo.model.inbox.Inbox;
import com.example.demo.publishers.DMPublisher;
import com.example.demo.repository.DMRepository;
import com.example.demo.repository.InboxRepository;

import reactor.core.publisher.Flux;

@Service
public class DirectMessageService {
  private final DMRepository dmRepository;
  private final InboxRepository inboxRepository;
  private final CurrentAuthenticatedUser currentAuthenticatedUser;
  private final DMPublisher dmPublisher;

  public DirectMessageService(DMRepository dmRepository, InboxRepository inboxRepository,
      CurrentAuthenticatedUser currentAuthenticatedUser, DMPublisher dmPublisher) {
    this.dmRepository = dmRepository;
    this.inboxRepository = inboxRepository;
    this.currentAuthenticatedUser = currentAuthenticatedUser;
    this.dmPublisher = dmPublisher;
  }

  public DirectMessage createDirectMessage(CreateDMInput input) {
    EndpointProtector.checkAuth();
    User user = currentAuthenticatedUser.getUser();
    Inbox inbox = inboxRepository.findById(input.getInboxId())
        .orElseThrow(() -> new ModifiedException("Inbox with this id does not exist"));
    DirectMessage message = new DirectMessage(input.getText(), input.getType(), inbox, user);
    dmPublisher.publish(input.getInboxId(), message);
    return dmRepository.save(message);
  }

  public DirectMessagePageDTO getMessagesByInboxId(Long inboxId, int page, int size, String search) {
    EndpointProtector.checkAuth();
    User user = currentAuthenticatedUser.getUser();

    Inbox inbox = inboxRepository.findById(inboxId).orElseThrow(() -> new ModifiedException("Inbox not found"));

    if (!inbox.getUsers().contains(user)) {
      throw new ModifiedException("Access denied: user has not access to this DM");
    }

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreated"));
    Page<DirectMessage> messagePage;

    if (search != null && !search.trim().isEmpty()) {
      messagePage = dmRepository.findByInboxIdAndTextContainingIgnoreCase(inboxId, search.trim(), pageable);
    } else {
      messagePage = dmRepository.findByInboxId(inboxId, pageable);
    }

    return new DirectMessagePageDTO(messagePage);
  }

  public Flux<DirectMessage> subscribeToMessagesByInboxId(Long inboxId) {
    return dmPublisher.subscribe(inboxId);
  }

}
