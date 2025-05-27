
package com.example.demo.publishers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.example.demo.model.message.Message;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class MessagePublisher {

  private final Map<Long, Sinks.Many<Message>> sinks = new ConcurrentHashMap<>();

  public void publish(Long roomId, Message message) {
    Sinks.Many<Message> sink = sinks.get(roomId);
    if (sink != null) {
      sink.tryEmitNext(message);
    }
  }

  public Flux<Message> subscribe(Long roomId) {
    Sinks.Many<Message> sink = sinks.computeIfAbsent(
        roomId,
        id -> Sinks.many().replay().latest() // Keeps the stream alive and replays latest message
    );
    return sink.asFlux();
  }
}
