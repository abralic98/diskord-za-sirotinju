package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.User;
import com.example.demo.model.inbox.Inbox;

public interface InboxRepository extends JpaRepository<Inbox, Long> {
  List<Inbox> findByUsers_Id(Long userId);

  @Query("SELECT i FROM Inbox i JOIN i.users u1 JOIN i.users u2 " +
      "WHERE SIZE(i.users) = 2 AND " +
      "(:user1 MEMBER i.users AND :user2 MEMBER i.users)")
  Optional<Inbox> findDirectInboxBetween(@Param("user1") User user1, @Param("user2") User user2);
}
