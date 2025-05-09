package com.example.demo.dto.message;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.demo.model.message.Message;

public class MessagePageDTO {
  private List<Message> content;
  private int totalPages;
  private long totalElements;
  private int number;
  private int size;

  public MessagePageDTO(Page<Message> page) {
    this.content = page.getContent();
    this.totalPages = page.getTotalPages();
    this.totalElements = page.getTotalElements();
    this.number = page.getNumber();
    this.size = page.getSize();
  }

  // Getters here...
}
