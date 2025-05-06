package com.example.demo.repository;

import com.example.demo.model.message.Message;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRoomId(Long id);
}
