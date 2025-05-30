package com.example.demo.controller.inbox;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.example.demo.controller.inputs.inbox.CreateDMInput;
import com.example.demo.dto.inbox.DirectMessagePageDTO;
import com.example.demo.model.inbox.DirectMessage;
import com.example.demo.service.inbox.DirectMessageService;

import reactor.core.publisher.Flux;

@Controller
public class DirectMessageController {

  private final DirectMessageService dmService;

  public DirectMessageController(DirectMessageService dmService) {
    this.dmService = dmService;
  }

  @MutationMapping
  public DirectMessage createDirectMessage(@Argument CreateDMInput message) {
    return dmService.createDirectMessage(message);
  }

  //
  @QueryMapping
  public DirectMessagePageDTO getDirectMessagesByInboxId(@Argument Long id, @Argument int page, @Argument int size,
      @Argument String search) {
    return dmService.getDirectMessagesByInboxId(id, page, size, search);
  }

  @SubscriptionMapping
  public Flux<DirectMessage> subscribeToMessagesByInboxId(@Argument Long inboxId) {
    try {
      return dmService.subscribeToMessagesByInboxId(inboxId);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

}
