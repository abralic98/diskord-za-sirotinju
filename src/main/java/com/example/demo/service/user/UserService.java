
// U SERVICE RADIMO LOGIKU
package com.example.demo.service.user;

import com.example.demo.config.EndpointProtector;
import com.example.demo.controller.global.ModifiedException;
import com.example.demo.controller.inputs.user.CreateUserInput;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service // Marks this class as a Spring service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  // Constructor-based dependency injection
  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  // Create a new user
  public User createUser(CreateUserInput user) {
    String encodedPassword = passwordEncoder.encode(user.getPassword());
    User newUser = new User(user.getUsername(), encodedPassword , user.getEmail()); // Create

    return userRepository.save(newUser);
  }

  // Get a user by their ID
  public Optional<User> getUserById(Long id) {
    EndpointProtector.checkAuth();
    try {
      Optional<User> user = userRepository.findById(id);
      System.out.println(user);
      return user;

    } catch (Exception e) {
      System.out.println("excepcija");
      return null;
    }
    // return userRepository.findById(id);
  }

  // Get all users (optional, if needed)
  public Iterable<User> getAllUsers() {
    EndpointProtector.checkAuth();
    return userRepository.findAll(); // Get all users from DB
  }

  public User deactivateUser(Long id) {
    Optional<User> user = userRepository.findById(id);
    if (user.isPresent()) {
      User currentUser = user.get();
      if (!currentUser.getIsUserActive()) {
        throw new ModifiedException("User already deactivated");
      }
      currentUser.setActivateUser(false);
      return userRepository.save(currentUser);
    } else {
      throw new ModifiedException("User not found");
    }
  }

}
