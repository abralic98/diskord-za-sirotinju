
package com.example.demo.dto.inbox;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.demo.model.inbox.DirectMessage;;

public class DirectMessagePageDTO {
  private List<DirectMessage> content;
  private int totalPages;
  private long totalElements;
  private int number;
  private int size;

  public DirectMessagePageDTO(Page<DirectMessage> page) {
    this.content = page.getContent();
    this.totalPages = page.getTotalPages();
    this.totalElements = page.getTotalElements();
    this.number = page.getNumber();
    this.size = page.getSize();
  }
}
