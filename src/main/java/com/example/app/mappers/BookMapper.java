package com.example.app.mappers;

import java.time.LocalDateTime;

import com.example.app.infrastructure.entities.AuthorEntity;
import com.example.app.infrastructure.entities.BookEntity;
import com.example.app.models.AuthorModel;
import com.example.app.models.BookModel;

public final class BookMapper {
    private BookMapper() {
    }

    public static BookModel toDomain(BookEntity entity) {
        AuthorModel author = AuthorMapper.toDomain(entity.getAuthor());
        BookModel model = new BookModel(entity.getTitle(), entity.getDescription(), author);
        model.setId(entity.getId());
        model.setPublishedAt(entity.getPublishedAt() != null ? entity.getPublishedAt().toString() : null);
        model.setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null);
        model.setIsActive(entity.getIsActive());
        model.setIsPublished(entity.getIsPublished());
        model.setIsDeleted(entity.getIsDeleted());
        model.setIsFeatured(entity.getIsFeatured());
        model.setIsTrending(entity.getIsTrending());
        model.setIsNew(entity.getIsNew());
        model.setIsPopular(entity.getIsPopular());
        model.setIsBestSeller(entity.getIsBestSeller());
        return model;
    }

    public static BookEntity toEntity(BookModel model, AuthorEntity authorEntity) {
        BookEntity entity = new BookEntity(model.getTitle(), model.getDescription(), authorEntity);
        entity.setIsActive(model.getIsActive());
        entity.setIsPublished(model.getIsPublished());
        entity.setIsDeleted(model.getIsDeleted());
        entity.setIsFeatured(model.getIsFeatured());
        entity.setIsTrending(model.getIsTrending());
        entity.setIsNew(model.getIsNew());
        entity.setIsPopular(model.getIsPopular());
        entity.setIsBestSeller(model.getIsBestSeller());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    public static void applyToEntity(BookModel model, BookEntity entity, AuthorEntity authorEntity) {
        entity.setTitle(model.getTitle());
        entity.setDescription(model.getDescription());
        entity.setAuthor(authorEntity);
        entity.setIsActive(model.getIsActive());
        entity.setIsPublished(model.getIsPublished());
        entity.setIsDeleted(model.getIsDeleted());
        entity.setIsFeatured(model.getIsFeatured());
        entity.setIsTrending(model.getIsTrending());
        entity.setIsNew(model.getIsNew());
        entity.setIsPopular(model.getIsPopular());
        entity.setIsBestSeller(model.getIsBestSeller());
        entity.setUpdatedAt(LocalDateTime.now());
    }
}
