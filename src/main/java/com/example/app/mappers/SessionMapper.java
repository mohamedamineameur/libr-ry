package com.example.app.mappers;

import com.example.app.infrastructure.entities.SessionEntity;
import com.example.app.infrastructure.entities.UserEntity;
import com.example.app.models.SessionModel;

public final class SessionMapper {
    private SessionMapper() {
    }

    public static SessionModel toDomain(SessionEntity entity) {
        SessionModel model = new SessionModel(
            UserMapper.toDomain(entity.getUser()),
            entity.getIpAddress(),
            entity.getUserAgent(),
            entity.getBrowser(),
            entity.getOs(),
            entity.getCountry(),
            entity.getCity(),
            entity.getRegion(),
            entity.getLatitude(),
            entity.getLongitude(),
            entity.getTimezone()
        );
        model.setId(entity.getId());
        model.setCreatedAt(entity.getCreatedAt());
        model.setExpiresAt(entity.getExpiresAt());
        model.setIsActive(entity.getIsActive());
        return model;
    }

    public static SessionEntity toEntity(SessionModel model, UserEntity userEntity) {
        return new SessionEntity(
            userEntity,
            model.getCreatedAt(),
            model.getExpiresAt(),
            model.getIsActive(),
            model.getIpAddress(),
            model.getUserAgent(),
            model.getBrowser(),
            model.getOs(),
            model.getCountry(),
            model.getCity(),
            model.getRegion(),
            model.getLatitude(),
            model.getLongitude(),
            model.getTimezone()
        );
    }
}
