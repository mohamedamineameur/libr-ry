package com.example.app.infrastructure.persistences;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.infrastructure.entities.BookEntity;
import com.example.app.infrastructure.entities.LaonEntity;
import com.example.app.infrastructure.entities.UserEntity;
import com.example.app.mappers.LaonMapper;
import com.example.app.models.LaonModel;
import com.example.app.repositories.LaonRepository;

@Repository
@Transactional
@SuppressWarnings("null")
public class LaonRepositoryImpl implements LaonRepository {

    private final LaonRepositoryJpa laonRepositoryJpa;
    private final UserRepositoryJpa userRepositoryJpa;
    private final BookRepositoryJpa bookRepositoryJpa;

    public LaonRepositoryImpl(
        LaonRepositoryJpa laonRepositoryJpa,
        UserRepositoryJpa userRepositoryJpa,
        BookRepositoryJpa bookRepositoryJpa
    ) {
        this.laonRepositoryJpa = laonRepositoryJpa;
        this.userRepositoryJpa = userRepositoryJpa;
        this.bookRepositoryJpa = bookRepositoryJpa;
    }

    @Override
    public LaonModel findById(UUID id) {
        return LaonMapper.toDomain(
            laonRepositoryJpa.findById(id).orElseThrow(() -> new RuntimeException("Loan not found"))
        );
    }

    @Override
    public List<LaonModel> findByUserId(UUID userId) {
        return laonRepositoryJpa.findByUserId(userId).stream().map(LaonMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public LaonModel findByBookId(UUID bookId) {
        return LaonMapper.toDomain(
            laonRepositoryJpa.findFirstByBookIdOrderByLoanDateDesc(bookId).orElseThrow(() -> new RuntimeException("Loan not found"))
        );
    }

    @Override
    public LaonModel save(LaonModel loan) {
        UserEntity userEntity = userRepositoryJpa.findById(loan.getUser().getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        BookEntity bookEntity = bookRepositoryJpa.findById(loan.getBook().getId())
            .orElseThrow(() -> new RuntimeException("Book not found"));

        LaonEntity entity = LaonMapper.toEntity(loan, userEntity, bookEntity);
        return LaonMapper.toDomain(laonRepositoryJpa.save(entity));
    }

    @Override
    public LaonModel updateReturning(UUID id, LaonModel loan) {
        LaonEntity existing = laonRepositoryJpa.findById(id).orElseThrow(() -> new RuntimeException("Loan not found"));
        UserEntity userEntity = userRepositoryJpa.findById(loan.getUser().getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        BookEntity bookEntity = bookRepositoryJpa.findById(loan.getBook().getId())
            .orElseThrow(() -> new RuntimeException("Book not found"));

        LaonMapper.applyToEntity(loan, existing, userEntity, bookEntity);
        return LaonMapper.toDomain(laonRepositoryJpa.save(existing));
    }

    @Override
    public List<LaonModel> findAll() {
        return laonRepositoryJpa.findAll().stream().map(LaonMapper::toDomain).collect(Collectors.toList());
    }
}
