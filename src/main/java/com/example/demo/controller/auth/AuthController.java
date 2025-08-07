
package com.example.demo.controller.auth;

import com.example.demo.config.JwtUtil;
import com.example.demo.controller.global.ModifiedException;
import com.example.demo.controller.inputs.user.CreateSessionInput;
import com.example.demo.model.User;
import com.example.demo.model.UserWithToken;
import com.example.demo.model.enums.UserPresenceType;
import com.example.demo.model.firebase.Firebase;
import com.example.demo.repository.FirebaseRepository;
import com.example.demo.repository.UserRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  private PasswordEncoder passwordEncoder;

  @Autowired
  private FirebaseRepository firebaseRepository;

  @MutationMapping
  public UserWithToken createSession(@Argument CreateSessionInput credentials) {

    String username = credentials.getUsername();
    String password = credentials.getPassword();
    String firebaseToken = credentials.getFirebaseToken();

    User user = userRepository.findByUsername(username).orElseThrow(() -> new ModifiedException("User Not Found"));
    Boolean isPasswordCorrect = passwordEncoder.matches(password, user.getPassword());
    Firebase firebase = new Firebase(firebaseToken, user);
    Optional<Firebase> existingToken = firebaseRepository.findByToken(firebaseToken);
    if (!isPasswordCorrect) {
      throw new ModifiedException("Invalid credentials");
    }
    if (user != null && isPasswordCorrect) {
      String token = jwtUtil.generateToken(username, user.getId());
      user.setUserPresence(UserPresenceType.ONLINE);
      user.setActivateUser(true);
      if (existingToken.isEmpty()) {
        firebaseRepository.save(firebase);
      }
      userRepository.save(user);
      UserWithToken userWithToken = new UserWithToken();
      userWithToken.setUser(user);
      userWithToken.setToken(token);

      return userWithToken;
    }

    throw new ModifiedException("Invalid credentials");
  }
}
