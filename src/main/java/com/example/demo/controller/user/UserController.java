
// U KONTROLERU PISEMO KVERIJE 
package com.example.demo.controller.user;

import com.example.demo.controller.inputs.user.CreateUserInput;
import com.example.demo.model.User;
import com.example.demo.service.user.UserService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
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

  @PreAuthorize("hasRole('MEMBER')")
  // GraphQL query for getting all users
  @QueryMapping
  public List<User> getAllUsers() {
    return (List<User>) userService.getAllUsers(); // Fetch all users from the service
  }

  // GraphQL query for getting a user by their ID
  @QueryMapping
  public Optional<User> getUserById(@Argument Long id) {
    return userService.getUserById(id); // Fetch user by ID from the service
  }

  // GraphQL mutation for creating a user
  @MutationMapping
  public User createUser(CreateUserInput user) {
    return userService.createUser(user); // Create a user via the service
  }

  @MutationMapping
  public User deactivateUser(@Argument Long id) {
    return userService.deactivateUser(id); // Create a user via the service
  }

}
