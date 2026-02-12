package com.example.app.infrastructure.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "security", uniqueConstraints = @UniqueConstraint(name = "uk_security_user", columnNames = "user_id"))
public class SecurityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "is_2fa_enabled", nullable = false)
    private boolean is2FAEnabled;

    @Column(name = "is_mail_verified", nullable = false)
    private boolean isMailVerified;

    @Column(name = "email_verification_token_hash")
    private String emailVerificationTokenHash;

    @Column(name = "email_verification_expires_at")
    private LocalDateTime emailVerificationExpiresAt;

    @Column(name = "otp_hash")
    private String otpHash;

    @Column(name = "otp_expires_at")
    private LocalDateTime otpExpiresAt;

    @Column(name = "secret_key_hash")
    private String secretKeyHash;

    protected SecurityEntity() {
    }

    public SecurityEntity(UserEntity user) {
        this.user = user;
        this.is2FAEnabled = false;
        this.isMailVerified = false;
    }

    public UUID getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public boolean getIs2FAEnabled() {
        return is2FAEnabled;
    }

    public boolean getIsMailVerified() {
        return isMailVerified;
    }

    public String getEmailVerificationTokenHash() {
        return emailVerificationTokenHash;
    }

    public LocalDateTime getEmailVerificationExpiresAt() {
        return emailVerificationExpiresAt;
    }

    public String getOtpHash() {
        return otpHash;
    }

    public LocalDateTime getOtpExpiresAt() {
        return otpExpiresAt;
    }

    public String getSecretKeyHash() {
        return secretKeyHash;
    }

    public void setIs2FAEnabled(boolean is2FAEnabled) {
        this.is2FAEnabled = is2FAEnabled;
    }

    public void setIsMailVerified(boolean isMailVerified) {
        this.isMailVerified = isMailVerified;
    }

    public void setEmailVerificationTokenHash(String emailVerificationTokenHash) {
        this.emailVerificationTokenHash = emailVerificationTokenHash;
    }

    public void setEmailVerificationExpiresAt(LocalDateTime emailVerificationExpiresAt) {
        this.emailVerificationExpiresAt = emailVerificationExpiresAt;
    }

    public void setOtpHash(String otpHash) {
        this.otpHash = otpHash;
    }

    public void setOtpExpiresAt(LocalDateTime otpExpiresAt) {
        this.otpExpiresAt = otpExpiresAt;
    }

    public void setSecretKeyHash(String secretKeyHash) {
        this.secretKeyHash = secretKeyHash;
    }
}
