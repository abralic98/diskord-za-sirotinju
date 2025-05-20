package com.example.demo.repository;

import com.example.demo.model.message.Message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
  Page<Message> findByRoomId(Long roomId, Pageable pageable);

  Page<Message> findByTextContainingIgnoreCase(String text, Pageable pageable);
}
