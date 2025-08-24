
package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.webmvc.GraphQlWebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.context.annotation.Bean;

import com.example.demo.repository.UserRepository;
import com.example.demo.websocket.VoiceWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  private final UserRepository userRepository;
  private final GraphQlWebSocketHandler graphQlWebSocketHandler;
  private final SubscriptionInterceptor subscriptionInterceptor;

  public WebSocketConfig(UserRepository userRepository,
      GraphQlWebSocketHandler graphQlWebSocketHandler,
      SubscriptionInterceptor subscriptionInterceptor) {
    this.userRepository = userRepository;
    this.graphQlWebSocketHandler = graphQlWebSocketHandler;
    this.subscriptionInterceptor = subscriptionInterceptor;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(voiceWebSocketHandler(), "/ws/voice")
        .setAllowedOrigins("*");

    // // GraphQL subscription WS
    // registry.addHandler(graphQlWebSocketHandler, "graphql")
    // .addInterceptors(subscriptionInterceptor)
    // .setAllowedOrigins("http://localhost:3000", "https://ezcomms.app");
  }

  @Bean
  public VoiceWebSocketHandler voiceWebSocketHandler() {
    return new VoiceWebSocketHandler();
  }
}
