
package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;


import com.example.demo.repository.UserRepository;
import com.example.demo.websocket.VoiceWebSocketHandler;

import org.springframework.context.annotation.Bean;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  private final UserRepository userRepository;

  public WebSocketConfig(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(voiceWebSocketHandler(), "/ws/voice")
        .setAllowedOrigins("*");
  }

  @Bean
  public VoiceWebSocketHandler voiceWebSocketHandler() {
    return new VoiceWebSocketHandler();
  }

}

