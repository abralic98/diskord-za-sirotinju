
package com.example.demo.controller.room;

import com.example.demo.service.room.VoiceRoomService;
import com.example.demo.dto.server.ServerVoiceRoomsDTO;
import com.example.demo.helpers.CurrentAuthenticatedUser;
import com.example.demo.model.User;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
public class VoiceRoomController {

  private final VoiceRoomService voiceRoomService;
  private final CurrentAuthenticatedUser currentAuthenticatedUser;

  public VoiceRoomController(VoiceRoomService voiceRoomService, CurrentAuthenticatedUser currentAuthenticatedUser) {
    this.voiceRoomService = voiceRoomService;
    this.currentAuthenticatedUser = currentAuthenticatedUser;
  }

  @SubscriptionMapping
  public Flux<ServerVoiceRoomsDTO> subscribeToServer(@Argument Long serverId, @Argument Long userId) {
    return voiceRoomService.subscribeToServer(serverId, userId);
  }

  @MutationMapping
  public Boolean joinVoiceRoom(@Argument Long serverId, @Argument Long roomId) {
    User user = currentAuthenticatedUser.getUser();
    voiceRoomService.joinRoom(serverId, roomId, user.getId());
    return true;
  }

  @MutationMapping
  public Boolean leaveVoiceRoom(@Argument Long serverId, @Argument Long roomId) {
    User user = currentAuthenticatedUser.getUser();
    voiceRoomService.leaveRoom(serverId, roomId, user.getId());
    return true;
  }

}
