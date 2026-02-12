package com.example.app.infrastructure.persistences;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.infrastructure.entities.SessionEntity;

public interface SessionRepositoryJpa extends JpaRepository<SessionEntity, UUID> {
    List<SessionEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
