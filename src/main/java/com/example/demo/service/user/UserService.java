
// U SERVICE RADIMO LOGIKU
package com.example.demo.service.user;

import com.example.demo.config.EndpointProtector;
import com.example.demo.controller.global.ModifiedException;
import com.example.demo.controller.inputs.user.*;
import com.example.demo.helpers.CurrentAuthenticatedUser;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service // Marks this class as a Spring service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CurrentAuthenticatedUser currentAuthenticatedUser;

  // Constructor-based dependency injection
  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
      CurrentAuthenticatedUser currentAuthenticatedUser) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.currentAuthenticatedUser = currentAuthenticatedUser;
  }

  public User getMe() {
    EndpointProtector.checkAuth();
    User user = currentAuthenticatedUser.getUser();
    return user;
  }

  public User createUser(CreateUserInput user) {
    String encodedPassword = passwordEncoder.encode(user.getPassword());

    userRepository.findByUsername(user.getUsername())
        .ifPresent(u -> {
          throw new ModifiedException("Username is already taken");
        });

    userRepository.findByEmail(user.getEmail())
        .ifPresent(u -> {
          throw new ModifiedException("Email is already taken");
        });

    User newUser = new User(user.getUsername(), encodedPassword, user.getEmail());
    return userRepository.save(newUser);
  }

  public User updateUser(UpdateUserInput user) {
    User currentUser = currentAuthenticatedUser.getUser();
    // debilan nacin sa ifovima vjerovatno ima neki normalniji nacin
    if (user.getUsername() != null) {
      currentUser.setUsername(user.getUsername());
    }
    if (user.getEmail() != null) {
      currentUser.setEmail(user.getEmail());
    }
    if (user.getPhoneNumber() != null) {
      currentUser.setPhoneNumber(user.getPhoneNumber());
    }
    if (user.getAvatar() != null) {
      currentUser.setAvatar(user.getAvatar());
    }

    User updatedUser = userRepository.save(currentUser);
    currentAuthenticatedUser.refreshAuthentication(updatedUser);
    return updatedUser;
  }

  public User updateUserPassword(UpdateUserPasswordInput credentials) {
    User currentUser = currentAuthenticatedUser.getUser();

    Boolean isPasswordCorrect = passwordEncoder.matches(credentials.getCurrentPassword(), currentUser.getPassword());
    if (!isPasswordCorrect) {
      throw new ModifiedException("Invalid current password");
    }
    if (!Objects.equals(credentials.getNewPassword(), credentials.getConfirmNewPassword())) {
      throw new ModifiedException("Password and Confirm password must match!");
    }
    if (credentials.getNewPassword() == null) {
      throw new ModifiedException("New password must not be null");
    }

    String encodedNewPassword = passwordEncoder.encode(credentials.getNewPassword());
    currentUser.setPassword(encodedNewPassword);
    User updatedUser = userRepository.save(currentUser);
    currentAuthenticatedUser.refreshAuthentication(updatedUser);
    return updatedUser;
  }

  public Optional<User> getUserById(Long id) {
    EndpointProtector.checkAuth();
    try {
      Optional<User> user = userRepository.findById(id);
      System.out.println(user);
      return user;

    } catch (Exception e) {
      return null;
    }
  }

  public Iterable<User> getAllUsers() {
    EndpointProtector.checkAuth();
    return userRepository.findAll();
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
