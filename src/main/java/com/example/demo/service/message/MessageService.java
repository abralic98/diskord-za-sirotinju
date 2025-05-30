package com.example.demo.service.message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.config.EndpointProtector;
import com.example.demo.controller.inputs.message.CreateMessageInput;
import com.example.demo.dto.message.MessagePageDTO;
import com.example.demo.helpers.CurrentAuthenticatedUser;
import com.example.demo.model.User;
import com.example.demo.model.message.Message;
import com.example.demo.model.room.Room;
import com.example.demo.model.server.Server;
import com.example.demo.publishers.MessagePublisher;
import com.example.demo.controller.global.ModifiedException;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.RoomRepository;

import reactor.core.publisher.Flux;

@Service
public class MessageService {

  private final RoomRepository roomRepository;
  private final MessageRepository messageRepository;
  private final CurrentAuthenticatedUser currentAuthenticatedUser;
  private final MessagePublisher messagePublisher;

  public MessageService(MessageRepository messageRepository, RoomRepository roomRepository,
      CurrentAuthenticatedUser currentAuthenticatedUser, MessagePublisher messagePublisher) {
    this.messageRepository = messageRepository;
    this.roomRepository = roomRepository;
    this.currentAuthenticatedUser = currentAuthenticatedUser;
    this.messagePublisher = messagePublisher;
  }

  public Message createMessage(CreateMessageInput input) {
    EndpointProtector.checkAuth();
    User user = currentAuthenticatedUser.getUser();

    Room room = roomRepository.findById(input.getRoomId())
        .orElseThrow(() -> new ModifiedException("Room with this id does not exist"));

    if ((input.getText() == null || input.getText().trim().isEmpty()) &&
        (input.getImageUrl() == null || input.getImageUrl().trim().isEmpty())) {
      throw new ModifiedException("Either text or imageUrl must be provided.");
    }

    Message message = new Message(
        input.getText(),
        input.getType(),
        input.getImageUrl(),
        room,
        user);

    messagePublisher.publish(input.getRoomId(), message);
    return messageRepository.save(message);
  }

  public MessagePageDTO getMessagesByRoomId(Long roomId, int page, int size, String search) {
    EndpointProtector.checkAuth();
    User user = currentAuthenticatedUser.getUser();

    Room room = roomRepository.findById(roomId).orElseThrow(() -> new ModifiedException("Room not found"));
    Server server = room.getServer();

    if (!server.getJoinedUsers().contains(user)) {
      throw new ModifiedException("Access denied: user has not joined the server");
    }

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreated"));
    Page<Message> messagePage;

    if (search != null && !search.trim().isEmpty()) {
      messagePage = messageRepository.findByRoomIdAndTextContainingIgnoreCase(roomId, search.trim(), pageable);
    } else {
      messagePage = messageRepository.findByRoomId(roomId, pageable);
    }

    return new MessagePageDTO(messagePage);
  }

  public Flux<Message> subscribeToMessagesByRoomId(Long roomId) {

    // auth problem kad stavim puca zato jer http radi ws ne radi nije isto
    // User user = currentAuthenticatedUser.getUser();
    // Room room = roomRepository.findById(roomId)
    // .orElseThrow(() -> new ModifiedException("Room not found"));
    //
    // if (!room.getServer().getJoinedUsers().contains(user)) {
    // throw new ModifiedException("Access denied: user has not joined the server");
    // }

    return messagePublisher.subscribe(roomId);
  }
}
