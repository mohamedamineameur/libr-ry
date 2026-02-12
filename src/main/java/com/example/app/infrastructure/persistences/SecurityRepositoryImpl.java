package com.example.app.infrastructure.persistences;

import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.infrastructure.entities.SecurityEntity;
import com.example.app.infrastructure.entities.UserEntity;
import com.example.app.mappers.SecurityMapper;
import com.example.app.models.SecurityModel;
import com.example.app.repositories.SecurityRepository;

@Repository
@Transactional
@SuppressWarnings("null")
public class SecurityRepositoryImpl implements SecurityRepository {

    private final SecurityRepositoryJpa securityRepositoryJpa;
    private final UserRepositoryJpa userRepositoryJpa;

    public SecurityRepositoryImpl(SecurityRepositoryJpa securityRepositoryJpa, UserRepositoryJpa userRepositoryJpa) {
        this.securityRepositoryJpa = securityRepositoryJpa;
        this.userRepositoryJpa = userRepositoryJpa;
    }

    @Override
    public SecurityModel save(SecurityModel security) {
        UUID userId = security.getUser().getId();
        UserEntity userEntity = userRepositoryJpa.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        SecurityEntity existing = securityRepositoryJpa.findByUserId(userId).orElse(null);
        if (existing == null) {
            return SecurityMapper.toDomain(securityRepositoryJpa.save(SecurityMapper.toEntity(security, userEntity)));
        }

        existing.setIs2FAEnabled(security.getIs2FAEnabled());
        existing.setIsMailVerified(security.getIsMailVerified());
        existing.setEmailVerificationTokenHash(security.getEmailVerificationTokenHash());
        existing.setEmailVerificationExpiresAt(security.getEmailVerificationExpiresAt());
        existing.setOtpHash(security.getOTPHash());
        existing.setOtpExpiresAt(security.getOTPExpiresAt());
        existing.setSecretKeyHash(security.getSecretKeyHash());
        return SecurityMapper.toDomain(securityRepositoryJpa.save(existing));
    }

    @Override
    public SecurityModel findByUserId(UUID userId) {
        return SecurityMapper.toDomain(
            securityRepositoryJpa.findByUserId(userId).orElseThrow(() -> new RuntimeException("Security not found"))
        );
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return securityRepositoryJpa.findByUserId(userId).isPresent();
    }
}
