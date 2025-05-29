package com.example.demo.dto.user;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.demo.model.User;


public class UserPageDTO {
  private List<User> content;
  private int totalPages;
  private long totalElements;
  private int number;
  private int size;

  public UserPageDTO(Page<User> page) {
    this.content = page.getContent();
    this.totalPages = page.getTotalPages();
    this.totalElements = page.getTotalElements();
    this.number = page.getNumber();
    this.size = page.getSize();
  }
}
