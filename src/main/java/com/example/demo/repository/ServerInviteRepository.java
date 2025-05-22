package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.invite.ServerInvite;

public interface ServerInviteRepository extends JpaRepository<ServerInvite, String> {
  Optional<ServerInvite> findByToken(String token);
}
