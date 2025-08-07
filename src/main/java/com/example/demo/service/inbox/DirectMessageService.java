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
import com.example.demo.model.firebase.Firebase;
import com.example.demo.model.inbox.DirectMessage;
import com.example.demo.model.inbox.Inbox;
import com.example.demo.publishers.DMPublisher;
import com.example.demo.repository.DMRepository;
import com.example.demo.repository.FirebaseRepository;
import com.example.demo.repository.InboxRepository;
import com.example.demo.service.firebase.FirebaseMessagingService;

import java.util.List;

import reactor.core.publisher.Flux;

@Service
public class DirectMessageService {
  private final DMRepository dmRepository;
  private final InboxRepository inboxRepository;
  private final CurrentAuthenticatedUser currentAuthenticatedUser;
  private final DMPublisher dmPublisher;
  private final FirebaseMessagingService firebaseMessagingService;

  public DirectMessageService(DMRepository dmRepository, InboxRepository inboxRepository,
      CurrentAuthenticatedUser currentAuthenticatedUser, DMPublisher dmPublisher,
      FirebaseMessagingService firebaseMessagingService) {
    this.dmRepository = dmRepository;
    this.inboxRepository = inboxRepository;
    this.currentAuthenticatedUser = currentAuthenticatedUser;
    this.dmPublisher = dmPublisher;
    this.firebaseMessagingService = firebaseMessagingService;
  }

  public DirectMessage createDirectMessage(CreateDMInput input) {
    EndpointProtector.checkAuth();
    User sender = currentAuthenticatedUser.getUser();
    Inbox inbox = inboxRepository.findById(input.getInboxId())
        .orElseThrow(() -> new ModifiedException("Inbox with this id does not exist"));

    if ((input.getText() == null || input.getText().trim().isEmpty()) &&
        (input.getImageUrl() == null || input.getImageUrl().trim().isEmpty())) {
      throw new ModifiedException("Either text or imageUrl must be provided.");
    }

    DirectMessage message = new DirectMessage(input.getText(), input.getType(), input.getImageUrl(), inbox, sender);
    DirectMessage savedMessage = dmRepository.save(message);

    inbox.setLastMessage(savedMessage);
    inboxRepository.save(inbox);

    dmPublisher.publish(input.getInboxId(), savedMessage);

    sendPushNotificationsToInboxUsers(savedMessage);

    return savedMessage;
  }

  private void sendPushNotificationsToInboxUsers(DirectMessage message) {
    Inbox inbox = message.getInbox();
    User author = message.getAuthor();
    String messageText = message.getText();
    String messageImageUrl = message.getImageUrl();

    List<User> usersInInbox = inbox.getUsers();

    for (User recipient : usersInInbox) {
      // Skip the sender
      if (recipient.getId().equals(author.getId())) {
        continue;
      }

      // Get all firebase tokens for this recipient (from the entity relationship)
      List<Firebase> tokens = recipient.getFirebaseTokens();

      for (Firebase tokenEntity : tokens) {
        String token = tokenEntity.getToken();
        if (token == null || token.trim().isEmpty()) {
          continue; // skip null or empty tokens
        }

        try {
          String title = "New message from " + author.getUsername();
          String body = (messageText != null && !messageText.isEmpty()) ? messageText : "You have a new message.";

          String avatarUrl = author.getAvatar();
          if (avatarUrl == null || avatarUrl.isBlank()) {
            avatarUrl = "https://i1.sndcdn.com/avatars-000645192900-91rdqy-t240x240.jpg";
          }

          firebaseMessagingService.sendNotification(token, title, body, messageImageUrl, avatarUrl);
        } catch (Exception e) {
          System.err.println("Failed to send notification to token " + token + ": " + e.getMessage());
        }
      }
    }
  }

  public DirectMessagePageDTO getDirectMessagesByInboxId(Long inboxId, int page, int size, String search) {
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
    // EndpointProtector.checkAuth();
    // User user = currentAuthenticatedUser.getUser();

    return dmPublisher.subscribe(inboxId);
  }

}
