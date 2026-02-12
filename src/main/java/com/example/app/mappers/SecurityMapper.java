package com.example.app.mappers;

import com.example.app.infrastructure.entities.SecurityEntity;
import com.example.app.infrastructure.entities.UserEntity;
import com.example.app.models.SecurityModel;

public final class SecurityMapper {
    private SecurityMapper() {
    }

    public static SecurityModel toDomain(SecurityEntity entity) {
        SecurityModel model = new SecurityModel(UserMapper.toDomain(entity.getUser()));
        model.setId(entity.getId());
        model.setIs2FAEnabled(entity.getIs2FAEnabled());
        model.setIsMailVerified(entity.getIsMailVerified());
        model.setEmailVerificationTokenHash(entity.getEmailVerificationTokenHash());
        model.setEmailVerificationExpiresAt(entity.getEmailVerificationExpiresAt());
        model.setOTPHash(entity.getOtpHash());
        model.setOTPExpiresAt(entity.getOtpExpiresAt());
        model.setSecretKeyHash(entity.getSecretKeyHash());
        return model;
    }

    public static SecurityEntity toEntity(SecurityModel model, UserEntity userEntity) {
        SecurityEntity entity = new SecurityEntity(userEntity);
        entity.setIs2FAEnabled(model.getIs2FAEnabled());
        entity.setIsMailVerified(model.getIsMailVerified());
        entity.setEmailVerificationTokenHash(model.getEmailVerificationTokenHash());
        entity.setEmailVerificationExpiresAt(model.getEmailVerificationExpiresAt());
        entity.setOtpHash(model.getOTPHash());
        entity.setOtpExpiresAt(model.getOTPExpiresAt());
        entity.setSecretKeyHash(model.getSecretKeyHash());
        return entity;
    }
}
