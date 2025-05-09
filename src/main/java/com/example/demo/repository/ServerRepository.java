package com.example.demo.repository;

import com.example.demo.model.User;
import com.example.demo.model.server.Server;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRepository extends JpaRepository<Server, Long> {
  List<Server> findByJoinedUsersContaining(User user);
}
