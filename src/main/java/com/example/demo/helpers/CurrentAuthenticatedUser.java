package com.example.demo.helpers;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentAuthenticatedUser {

  private final UserRepository userRepository;

  public CurrentAuthenticatedUser(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User getUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new RuntimeException("No authenticated user found");
    }

    String username = authentication.getName();
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));
  }
}
