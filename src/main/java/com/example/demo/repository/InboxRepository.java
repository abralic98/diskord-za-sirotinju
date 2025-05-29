package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.inbox.Inbox;

public interface InboxRepository extends JpaRepository<Inbox, Long> {
  List<Inbox> findByUsers_Id(Long userId);
}
