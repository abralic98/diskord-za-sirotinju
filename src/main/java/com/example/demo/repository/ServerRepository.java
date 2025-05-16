package com.example.demo.repository;

import com.example.demo.model.User;
import com.example.demo.model.server.Server;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServerRepository extends JpaRepository<Server, Long> {
  @Query("SELECT s FROM Server s JOIN s.joinedUsers u WHERE u = :user ORDER BY s.name ASC")
  List<Server> findServersByUserOrderedByName(@Param("user") User user);
}
