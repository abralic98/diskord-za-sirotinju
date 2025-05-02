package com.example.demo.controller.message;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.example.demo.controller.inputs.message.CreateMessageInput;
import com.example.demo.model.message.Message;
import com.example.demo.service.message.MessageService;

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

  // @QueryMapping
  // public List<Message> getMessagesByRoomId() {
  // return messageService.getMessagesByRoomId();
  // }

}
