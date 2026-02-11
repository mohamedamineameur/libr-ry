package com.example.app.infrastructure.persistences;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.app.infrastructure.entities.UserEntity;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryJpa extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
}