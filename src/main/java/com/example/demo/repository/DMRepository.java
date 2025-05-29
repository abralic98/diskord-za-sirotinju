package com.example.demo.repository;

import com.example.demo.model.inbox.DirectMessage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DMRepository extends JpaRepository<DirectMessage, Long> {
  Page<DirectMessage> findByInboxId(Long inboxId, Pageable pageable);

  Page<DirectMessage> findByInboxIdAndTextContainingIgnoreCase(Long roomId, String text, Pageable pageable);
}
