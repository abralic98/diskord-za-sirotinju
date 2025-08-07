package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.firebase.Firebase;

@Repository
public interface FirebaseRepository extends JpaRepository<Firebase, Long> {
    Optional<Firebase> findByToken(String token);
}
