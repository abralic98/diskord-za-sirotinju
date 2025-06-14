package com.example.demo.repository;

import com.example.demo.model.User;
import com.example.demo.model.server.Server;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServerRepository extends JpaRepository<Server, Long> {
  @Query("SELECT s FROM Server s JOIN s.joinedUsers u WHERE u = :user ORDER BY s.name ASC")
  List<Server> findServersByUserOrderedByName(@Param("user") User user);

  // Page<Server> findByPublicServerTrueAndNameContainingIgnoreCase(String name, Pageable pageable);

  // Page<Server> findByPublicServerTrue(Pageable pageable);

  @Query("SELECT s FROM Server s WHERE s.publicServer = true AND LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) AND s.id NOT IN :bannedServerIds")
  Page<Server> findByNameAndNotBanned(@Param("search") String search,
      @Param("bannedServerIds") List<Long> bannedServerIds, Pageable pageable);

  @Query("SELECT s FROM Server s WHERE s.publicServer = true AND s.id NOT IN :bannedServerIds")
  Page<Server> findAllPublicAndNotBanned(@Param("bannedServerIds") List<Long> bannedServerIds, Pageable pageable);
}
