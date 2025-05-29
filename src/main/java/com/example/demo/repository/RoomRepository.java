package com.example.demo.repository;

import com.example.demo.model.room.Room;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
  //crna magija kako on prepozna ovo po imenu funkcije pitaj boga
  List<Room> findByServerId(Long serverId);
}
