
// U KONTROLERU PISEMO KVERIJE 
package com.example.demo.controller.user;

import com.example.demo.controller.inputs.user.CreateUserInput;
import com.example.demo.model.User;
import com.example.demo.service.user.UserService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller // Marks this class as a GraphQL controller
public class UserController {

  private final UserService userService;

  // Constructor injection of UserService
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @QueryMapping
  public User meQuery() {
    return userService.getMe();
  }

  @QueryMapping
  public List<User> getAllUsers() {
    return (List<User>) userService.getAllUsers();
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
  public User updateUser(@Argument CreateUserInput user) {
    return userService.createUser(user);
  }

  @MutationMapping
  public User deactivateUser(@Argument Long id) {
    return userService.deactivateUser(id);
  }

}
