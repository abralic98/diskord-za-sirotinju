package com.example.demo.controller.message;

import org.springframework.graphql.data.method.annotation.QueryMapping;

import java.util.List;

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

  // u raspaloj javi ne mozes stavit name argumenta drugaciji od onoga u schemi
  // znaci controler i service funkcija mora primat isto ime argumenta kao u schemi 2h izgubljeno unlucky
  // ALTERNATIVA public List<Message> getMessagesByRoomId(@Argument("roomId") Long id)  boze sacuvaj
  // kad pogledas donekle i ima smisla jer svaki put moram stavljat raspali @Argument da uspijem pokrenit projekt
  // ali bitno da se ne crveni nista ako zaboravim stavit unlucky
  @QueryMapping
  public List<Message> getMessagesByRoomId(@Argument Long id) {
    return messageService.getMessagesByRoomId(id);
  }

}
