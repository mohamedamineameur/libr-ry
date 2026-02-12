package com.example.app.infrastructure.persistences;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.infrastructure.entities.BookEntity;

public interface BookRepositoryJpa extends JpaRepository<BookEntity, UUID> {
    Optional<BookEntity> findByIdAndIsDeletedFalse(UUID id);

    List<BookEntity> findAllByIsDeletedFalse();
}
