package com.example.demo.repository;

import com.example.demo.model.server.Server;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRepository extends JpaRepository<Server, Long> {

}
