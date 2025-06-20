
package com.example.demo.repository;

import com.example.demo.model.User;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
  Optional<User> findByEmail(String email);

  Page<User> findByUsernameContainingIgnoreCase(String text, Pageable pageable);
}
