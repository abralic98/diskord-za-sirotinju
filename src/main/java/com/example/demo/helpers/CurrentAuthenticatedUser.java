package com.example.demo.helpers;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

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

    Object principal = authentication.getPrincipal();
    if (!(principal instanceof Long)) {
      throw new RuntimeException("Invalid principal");
    }

    Long userId = (Long) principal;
    return userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
  }

  public void refreshAuthentication(User updatedUser) {
    Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

    Authentication newAuth = new UsernamePasswordAuthenticationToken(
        updatedUser.getId(),  
        currentAuth.getCredentials(),
        currentAuth.getAuthorities()
    );

    SecurityContextHolder.getContext().setAuthentication(newAuth);
  }
}
