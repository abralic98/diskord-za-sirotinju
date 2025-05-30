
package com.example.demo.publishers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.example.demo.model.inbox.DirectMessage;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class DMPublisher {

  private final Map<Long, Sinks.Many<DirectMessage>> sinks = new ConcurrentHashMap<>();

  public void publish(Long inboxId, DirectMessage message) {
    Sinks.Many<DirectMessage> sink = sinks.get(inboxId);
    if (sink != null) {
      sink.tryEmitNext(message);
    }
  }

  public Flux<DirectMessage> subscribe(Long roomId) {
    return sinks.compute(roomId, (id, existingSink) -> {
      if (existingSink == null || existingSink.currentSubscriberCount() == 0) {
        return Sinks.many().multicast().onBackpressureBuffer();
      }
      return existingSink;
    }).asFlux();
  }
}
