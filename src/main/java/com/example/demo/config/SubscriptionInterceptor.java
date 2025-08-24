package com.example.demo.config;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
public class SubscriptionInterceptor implements HandshakeInterceptor {

  @Autowired
  private JwtUtil jwtUtil;

  //todo implementiraj ovo kako treba
  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

    if (request instanceof ServletServerHttpRequest servletRequest) {
      String token = servletRequest.getServletRequest().getParameter("token");
      if (token == null) {
        String authHeader = servletRequest.getServletRequest().getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
          token = authHeader.substring(7);
        }
      }

      if (token != null && jwtUtil.validateToken(token)) {
        Long userId = jwtUtil.extractUserId(token);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userId, null, List.of());

        // set into SecurityContext
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // store context in attributes so GraphQL can propagate it
        attributes.put("SPRING_SECURITY_CONTEXT", context);

        return true;
      }
    }

    response.setStatusCode(HttpStatus.UNAUTHORIZED);
    return false;
  }

  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Exception exception) {
    // Optional post-handshake logic
  }
}
