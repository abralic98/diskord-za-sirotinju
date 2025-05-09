
// src/main/java/com/example/demo/config/WebSocketConfig.java

package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

import com.example.demo.websocket.VoiceWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new VoiceWebSocketHandler(), "/ws/voice")
                .setAllowedOrigins("*");
    }
}
