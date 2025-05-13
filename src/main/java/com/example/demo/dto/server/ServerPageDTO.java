package com.example.demo.dto.server;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.demo.model.server.Server;

public class ServerPageDTO {
  private List<Server> content;
  private int totalPages;
  private long totalElements;
  private int number;
  private int size;

  public ServerPageDTO(Page<Server> page) {
    this.content = page.getContent();
    this.totalPages = page.getTotalPages();
    this.totalElements = page.getTotalElements();
    this.number = page.getNumber();
    this.size = page.getSize();
  }

  // Getters here...
}
