package com.example.app.infrastructure.persistences;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.infrastructure.entities.BookEntity;

public interface BookRepositoryJpa extends JpaRepository<BookEntity, UUID> {
}
