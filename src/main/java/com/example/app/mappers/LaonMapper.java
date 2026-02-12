package com.example.app.mappers;

import com.example.app.infrastructure.entities.BookEntity;
import com.example.app.infrastructure.entities.LaonEntity;
import com.example.app.infrastructure.entities.UserEntity;
import com.example.app.models.BookModel;
import com.example.app.models.LaonModel;
import com.example.app.models.UserModel;

public final class LaonMapper {
    private LaonMapper() {
    }

    public static LaonModel toDomain(LaonEntity entity) {
        UserModel user = UserMapper.toDomain(entity.getUser());
        BookModel book = BookMapper.toDomain(entity.getBook());

        LaonModel model = new LaonModel(user, book);
        model.setId(entity.getId());
        model.setLoanDate(entity.getLoanDate() != null ? entity.getLoanDate().toString() : null);
        model.setReturnDate(entity.getReturnDate() != null ? entity.getReturnDate().toString() : null);
        model.setIsReturned(entity.getIsReturned());
        return model;
    }

    public static LaonEntity toEntity(LaonModel model, UserEntity userEntity, BookEntity bookEntity) {
        LaonEntity entity = new LaonEntity(userEntity, bookEntity);
        entity.setIsReturned(model.getIsReturned());
        return entity;
    }

    public static void applyToEntity(LaonModel model, LaonEntity entity, UserEntity userEntity, BookEntity bookEntity) {
        entity.setUser(userEntity);
        entity.setBook(bookEntity);
        entity.setIsReturned(model.getIsReturned());
    }
}
