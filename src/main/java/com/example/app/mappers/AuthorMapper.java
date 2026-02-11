package com.example.app.mappers;

import com.example.app.infrastructure.entities.AuthorEntity;
import com.example.app.models.AuthorModel;

public final class AuthorMapper {
    private AuthorMapper() {
    }

    public static AuthorModel toDomain(AuthorEntity entity) {
        AuthorModel model = new AuthorModel(
            entity.getFirstName(),
            entity.getLastName(),
            entity.getEmail(),
            entity.getBiography()
        );
        model.setId(entity.getId());
        return model;
    }

    public static AuthorEntity toEntity(AuthorModel model) {
        return new AuthorEntity(
            model.getFirstName(),
            model.getLastName(),
            model.getEmail(),
            model.getBiography()
        );
    }
}
