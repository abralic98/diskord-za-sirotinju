
package com.example.demo.publishers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.example.demo.model.room.Room;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class RoomPublisher {

  private final Map<Long, Sinks.Many<Room>> sinks = new ConcurrentHashMap<>();

  public void publish(Long serverId, Room room) {
    Sinks.Many<Room> sink = sinks.get(serverId);
    if (sink != null) {
      sink.tryEmitNext(room);
    }
  }

  public Flux<Room> subscribe(Long serverId) {
    return sinks.compute(serverId, (id, existingSink) -> {
      if (existingSink == null || existingSink.currentSubscriberCount() == 0) {
        return Sinks.many().multicast().onBackpressureBuffer();
      }
      return existingSink;
    }).asFlux();
  }
}
