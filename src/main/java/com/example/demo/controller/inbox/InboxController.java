
package com.example.demo.controller.inbox;

import org.springframework.graphql.data.method.annotation.QueryMapping;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.example.demo.model.inbox.Inbox;
import com.example.demo.service.inbox.InboxService;

@Controller
public class InboxController {

  private final InboxService inboxService;

  public InboxController(InboxService inboxService) {
    this.inboxService = inboxService;
  }

  @MutationMapping
  public Inbox createInbox(@Argument Long withUserId) {
    return inboxService.createInbox(withUserId);
  }

  @MutationMapping
  public Boolean removeMeFromInbox(@Argument Long inboxId) {
    return inboxService.removeMeFromInbox(inboxId);
  }

  @QueryMapping
  public List<Inbox> getMyInbox() {
    return inboxService.getMyInbox();
  }

  @QueryMapping
  public Inbox getInboxById(@Argument Long id) {
    return inboxService.getInboxById(id);
  }

}
