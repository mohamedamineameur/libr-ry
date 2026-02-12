package com.example.app.models;

import java.util.UUID;
import java.time.LocalDateTime;

public class SecurityModel {
    private UUID id;
    private UserModel user;
    private boolean is2FAEnabled;
    private boolean isMailVerified;
    private String emailVerificationTokenHash;
    private LocalDateTime emailVerificationExpiresAt;
    private String OTPHash;
    private LocalDateTime OTPExpiresAt;
    private String SecretKeyHash;

    public SecurityModel(UserModel user) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.is2FAEnabled = false;
        this.isMailVerified = false;
        this.emailVerificationTokenHash = null;
        this.emailVerificationExpiresAt = null;
        this.OTPHash = null;
        this.OTPExpiresAt = null;
        this.SecretKeyHash = null;
    }

    // getters
    public UUID getId() {
        return id;
    }
    public UserModel getUser() {
        return user;
    }
    public boolean getIs2FAEnabled() {
        return is2FAEnabled;
    }
    public String getEmailVerificationTokenHash() {
        return emailVerificationTokenHash;
    }
    public LocalDateTime getEmailVerificationExpiresAt() {
        return emailVerificationExpiresAt;
    }
    public String getOTPHash() {
        return OTPHash;
    }
    public LocalDateTime getOTPExpiresAt() {
        return OTPExpiresAt;
    }
    public String getSecretKeyHash() {
        return SecretKeyHash;
    }
    public boolean getIsMailVerified() {
        return isMailVerified;
    }

    // setters
   
    public void setUser(UserModel user) {
        this.user = user;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setIs2FAEnabled(boolean is2FAEnabled) {
        this.is2FAEnabled = is2FAEnabled;
    }
    public void setEmailVerificationTokenHash(String emailVerificationTokenHash) {
        this.emailVerificationTokenHash = emailVerificationTokenHash;
    }
    public void setEmailVerificationExpiresAt(LocalDateTime emailVerificationExpiresAt) {
        this.emailVerificationExpiresAt = emailVerificationExpiresAt;
    }
    public void setOTPHash(String OTPHash) {
        this.OTPHash = OTPHash;
    }
    public void setOTPExpiresAt(LocalDateTime OTPExpiresAt) {
        this.OTPExpiresAt = OTPExpiresAt;
    }
    public void setSecretKeyHash(String SecretKeyHash) {
        this.SecretKeyHash = SecretKeyHash;
    }
    public void setIsMailVerified(boolean isMailVerified) {
        this.isMailVerified = isMailVerified;
    }


}
