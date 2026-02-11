package com.example.app.repositories;

import java.util.List;
import java.util.UUID;

import com.example.app.models.AuthorModel;

public interface AuthorRepository {
    AuthorModel findById(UUID id);

    AuthorModel findByEmail(String email);

    boolean existsByEmail(String email);

    AuthorModel save(AuthorModel author);

    AuthorModel update(UUID id, String firstName, String lastName, String email, String biography);

    List<AuthorModel> findAll();

    void delete(UUID id);
}
