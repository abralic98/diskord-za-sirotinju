package com.example.demo.service.message;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.config.EndpointProtector;
import com.example.demo.controller.inputs.message.CreateMessageInput;
import com.example.demo.helpers.CurrentAuthenticatedUser;
import com.example.demo.model.User;
import com.example.demo.model.message.Message;
import com.example.demo.model.room.Room;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.RoomRepository;

@Service
public class MessageService {

  private final RoomRepository roomRepository;
  private final MessageRepository messageRepository;
  private final CurrentAuthenticatedUser currentAuthenticatedUser;

  public MessageService(MessageRepository messageRepository, RoomRepository roomRepository,
      CurrentAuthenticatedUser currentAuthenticatedUser) {
    this.messageRepository = messageRepository;
    this.roomRepository = roomRepository;
    this.currentAuthenticatedUser = currentAuthenticatedUser;
  }

  public Optional<Room> getRoomById(Long id) {
    EndpointProtector.checkAuth();
    try {
      Optional<Room> room = roomRepository.findById(id);
      return room;
    } catch (Exception e) {
      return null;
    }
  }

  public Message createMessage(CreateMessageInput input) {
    EndpointProtector.checkAuth();
    User user = currentAuthenticatedUser.getUser();
    Room room = roomRepository.getById(input.getRoomId());
    Message message = new Message(input.getText(), input.getType(), room, user);
    return messageRepository.save(message);
  }

  public List<Room> getRoomsByServerId(Long id) {
    EndpointProtector.checkAuth();
    List<Room> rooms = roomRepository.findByServerId(id);
    return rooms;
  }
}
