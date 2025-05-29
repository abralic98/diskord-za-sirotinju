package com.example.demo.controller.inbox;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.example.demo.controller.inputs.inbox.CreateDMInput;
import com.example.demo.controller.inputs.message.CreateMessageInput;
import com.example.demo.dto.message.MessagePageDTO;
import com.example.demo.model.inbox.DirectMessage;
import com.example.demo.model.message.Message;
import com.example.demo.service.inbox.DirectMessageService;
import com.example.demo.service.message.MessageService;

import reactor.core.publisher.Flux;

@Controller
public class DirectMessageController {

  private final DirectMessageService dmService;

  public DirectMessageController(DirectMessageService dmService) {
    this.dmService = dmService;
  }

  // @MutationMapping
  // public DirectMessage createMessage(@Argument CreateDMInput message) {
  //   return dmService.createMessage(message);
  // }
  //
  // @QueryMapping
  // public MessagePageDTO getMessagesByInboxId(@Argument Long id, @Argument int page, @Argument int size,
  //     @Argument String search) {
  //   return dmService.getMessagesByRoomId(id, page, size, search);
  // }
  //
  // @SubscriptionMapping
  // public Flux<Message> messageAdded(@Argument Long roomId) {
  //   try {
  //     return dmService.messageAdded(roomId);
  //   } catch (Exception e) {
  //     e.printStackTrace();
  //     throw e;
  //   }
  // }

}
