package com.example.app.infrastructure.persistences;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.infrastructure.entities.AuthorEntity;

public interface AuthorRepositoryJpa extends JpaRepository<AuthorEntity, UUID> {
    Optional<AuthorEntity> findByEmail(String email);

    Optional<AuthorEntity> findByIdAndIsDeletedFalse(UUID id);

    Optional<AuthorEntity> findByEmailAndIsDeletedFalse(String email);

    List<AuthorEntity> findAllByIsDeletedFalse();
}
