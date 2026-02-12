package com.example.app.infrastructure.persistences;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.infrastructure.entities.SecurityEntity;

public interface SecurityRepositoryJpa extends JpaRepository<SecurityEntity, UUID> {
    Optional<SecurityEntity> findByUserId(UUID userId);
}
