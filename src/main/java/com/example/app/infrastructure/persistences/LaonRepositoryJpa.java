package com.example.app.infrastructure.persistences;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.infrastructure.entities.LaonEntity;

public interface LaonRepositoryJpa extends JpaRepository<LaonEntity, UUID> {
    List<LaonEntity> findByUserId(UUID userId);
    Page<LaonEntity> findByUserId(UUID userId, Pageable pageable);

    Optional<LaonEntity> findFirstByBookIdOrderByLoanDateDesc(UUID bookId);
}
