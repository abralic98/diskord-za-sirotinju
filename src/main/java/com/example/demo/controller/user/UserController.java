package com.example.demo.controller.user;

import com.example.demo.controller.inputs.user.*;
import com.example.demo.dto.user.UserPageDTO;
import com.example.demo.model.User;
import com.example.demo.service.user.UserService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @QueryMapping
  public User meQuery() {
    return userService.getMe();
  }

  @QueryMapping
  public UserPageDTO getAllUsers(@Argument int page, @Argument int size, @Argument String search) {
    return userService.getAllUsers(page, size, search);
  }

  @QueryMapping
  public Optional<User> getUserById(@Argument Long id) {
    return userService.getUserById(id);
  }

  @MutationMapping
  public User createUser(@Argument CreateUserInput user) {
    return userService.createUser(user);
  }

  @MutationMapping
  public User updateUser(@Argument UpdateUserInput user) {
    return userService.updateUser(user);
  }

  @MutationMapping
  public User updateUserPassword(@Argument UpdateUserPasswordInput credentials) {
    return userService.updateUserPassword(credentials);
  }

  @MutationMapping
  public User deactivateUser(@Argument Long id) {
    return userService.deactivateUser(id);
  }

}
