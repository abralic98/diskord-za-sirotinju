
package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.User;
import com.example.demo.model.firebase.Firebase;

@Repository
public interface FirebaseRepository extends JpaRepository<Firebase, Long> {

  Optional<Firebase> findByToken(String token);

  // ✅ Updated to reflect ManyToMany relationship
  List<Firebase> findByUsers_Id(Long userId);

  // Optional: if you already have the User object
  List<Firebase> findByUsers(User user);
}
