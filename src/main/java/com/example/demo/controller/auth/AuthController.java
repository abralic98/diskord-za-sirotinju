
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

import java.util.ArrayList;
import java.util.List;
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

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ModifiedException("User Not Found"));

    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new ModifiedException("Invalid credentials");
    }

    user.setUserPresence(UserPresenceType.ONLINE);
    user.setActivateUser(true);

    // Find existing Firebase token
    Optional<Firebase> existingTokenOpt = firebaseRepository.findByToken(firebaseToken);
    Firebase firebaseTokenEntity;

    if (existingTokenOpt.isPresent()) {
      firebaseTokenEntity = existingTokenOpt.get();
      if (!firebaseTokenEntity.getUsers().contains(user)) {
        firebaseTokenEntity.getUsers().add(user);
      }
    } else {
      firebaseTokenEntity = new Firebase();
      firebaseTokenEntity.setToken(firebaseToken);
      firebaseTokenEntity.setUsers(new ArrayList<>(List.of(user)));
    }

    if (!user.getFirebaseTokens().contains(firebaseTokenEntity)) {
      user.getFirebaseTokens().add(firebaseTokenEntity);
    }

    firebaseRepository.save(firebaseTokenEntity);
    userRepository.save(user);

    String token = jwtUtil.generateToken(username, user.getId());

    UserWithToken userWithToken = new UserWithToken();
    userWithToken.setUser(user);
    userWithToken.setToken(token);

    return userWithToken;
  }
}
