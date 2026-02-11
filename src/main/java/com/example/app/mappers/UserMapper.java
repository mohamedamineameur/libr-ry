package com.example.app.mappers;

import com.example.app.infrastructure.entities.UserEntity;
import com.example.app.models.UserModel;

public final class UserMapper {
    private UserMapper() {}
    public static UserModel toDomain(UserEntity entity) {
        UserModel model = new UserModel(entity.getName(), entity.getEmail(), entity.getPassword());
        model.setIsActive(entity.getIsActive());
        model.setIsAdmin(entity.getIsAdmin());
        return model;
    }
    public static UserEntity toEntity(UserModel model) {
        UserEntity entity = new UserEntity(model.getName(), model.getEmail(), model.getPassword());
        entity.setIsActive(model.getIsActive());
        entity.setIsAdmin(model.getIsAdmin());
        return entity;
    }
}
