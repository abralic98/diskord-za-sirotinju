
package com.example.demo.controller.auth;

import com.example.demo.config.JwtUtil;
import com.example.demo.controller.inputs.user.CreateSessionInput;
import com.example.demo.model.User;
import com.example.demo.model.UserWithToken;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  // GraphQL Mutation to handle login
  @MutationMapping
  public UserWithToken createSession(@Argument CreateSessionInput credentials) { // Use @Argument here instead of
                                                                                 // @RequestBody
    String username = credentials.getUsername();
    String password = credentials.getPassword();

    // Validate the credentials
    User user = userRepository.findByUsername(username);
    if (user != null && user.getPassword().equals(password)) {
      // Generate token
      String token = jwtUtil.generateToken(username);

      // Return UserWithToken containing both user and token
      UserWithToken userWithToken = new UserWithToken();
      userWithToken.setUser(user);
      userWithToken.setToken(token);

      return userWithToken;
    }

    throw new RuntimeException("Invalid credentials");
  }
}
