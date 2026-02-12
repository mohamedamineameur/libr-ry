package com.example.app.infrastructure.persistences;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.infrastructure.entities.AuthorEntity;
import com.example.app.mappers.AuthorMapper;
import com.example.app.models.AuthorModel;
import com.example.app.repositories.AuthorRepository;

@Repository
@Transactional
@SuppressWarnings("null")
public class AuthorRepositoryImpl implements AuthorRepository {

    private final AuthorRepositoryJpa authorRepositoryJpa;

    public AuthorRepositoryImpl(AuthorRepositoryJpa authorRepositoryJpa) {
        this.authorRepositoryJpa = authorRepositoryJpa;
    }

    @Override
    public AuthorModel findById(UUID id) {
        return AuthorMapper.toDomain(
            authorRepositoryJpa.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new RuntimeException("Author not found"))
        );
    }

    @Override
    public AuthorModel findByEmail(String email) {
        return AuthorMapper.toDomain(
            authorRepositoryJpa.findByEmailAndIsDeletedFalse(email).orElseThrow(() -> new RuntimeException("Author not found"))
        );
    }

    @Override
    public boolean existsByEmail(String email) {
        return authorRepositoryJpa.findByEmail(email).isPresent();
    }

    @Override
    public AuthorModel save(AuthorModel author) {
        return AuthorMapper.toDomain(authorRepositoryJpa.save(AuthorMapper.toEntity(author)));
    }

    @Override
    public AuthorModel update(UUID id, String firstName, String lastName, String email, String biography) {
        AuthorEntity author = authorRepositoryJpa.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new RuntimeException("Author not found"));
        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setEmail(email);
        author.setBiography(biography);
        return AuthorMapper.toDomain(authorRepositoryJpa.save(author));
    }

    @Override
    public List<AuthorModel> findAll() {
        return authorRepositoryJpa.findAllByIsDeletedFalse().stream().map(AuthorMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        AuthorEntity author = authorRepositoryJpa.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new RuntimeException("Author not found"));
        author.setIsDeleted(true);
        authorRepositoryJpa.save(author);
    }
}
