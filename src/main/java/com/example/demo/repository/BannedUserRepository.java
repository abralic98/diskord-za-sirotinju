package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.User;
import com.example.demo.model.server.Server;
import com.example.demo.model.user.BannedUser;

@Repository
public interface BannedUserRepository extends JpaRepository<BannedUser, Long> {
  @Query("SELECT bu.server.id FROM BannedUser bu WHERE bu.user.id = :userId")
  List<Long> findBannedServerIdsByUserId(@Param("userId") Long userId);

  Optional<BannedUser> findByUserAndServer(User user, Server server);
}
