package com.example.demo.controller.message;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.example.demo.controller.inputs.message.CreateMessageInput;
import com.example.demo.dto.message.MessagePageDTO;
import com.example.demo.model.message.Message;
import com.example.demo.service.message.MessageService;

import reactor.core.publisher.Flux;

@Controller
public class MessageController {

  private final MessageService messageService;

  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  @MutationMapping
  public Message createMessage(@Argument CreateMessageInput message) {
    return messageService.createMessage(message);
  }

  @QueryMapping
  public MessagePageDTO getMessagesByRoomId(@Argument Long id, @Argument int page, @Argument int size,
      @Argument String search) {
    return messageService.getMessagesByRoomId(id, page, size, search);
  }

  @SubscriptionMapping
  public Flux<Message> messageAdded(@Argument Long roomId) {
    try {
      return messageService.messageAdded(roomId);
    } catch (Exception e) {
      e.printStackTrace(); 
      throw e;
    }
  }

}
