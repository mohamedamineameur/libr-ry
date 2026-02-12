package com.example.app.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.exceptions.BusinessException;
import com.example.app.exceptions.NotFoundException;
import com.example.app.models.SecurityModel;
import com.example.app.models.UserModel;
import com.example.app.repositories.SecurityRepository;
import com.example.app.security.ResourceAuthorizationService;

@Service
@Transactional
public class SecurityService {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final SecurityRepository securityRepository;
    private final ResourceAuthorizationService resourceAuthorizationService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final MessageSource messageSource;
    private final long emailVerificationExpirationSeconds;
    private final long otpExpirationSeconds;

    public SecurityService(
        SecurityRepository securityRepository,
        ResourceAuthorizationService resourceAuthorizationService,
        BCryptPasswordEncoder passwordEncoder,
        EmailService emailService,
        MessageSource messageSource,
        @Value("${security.email-verification.expiration-seconds:86400}") long emailVerificationExpirationSeconds,
        @Value("${security.otp.expiration-seconds:300}") long otpExpirationSeconds
    ) {
        this.securityRepository = securityRepository;
        this.resourceAuthorizationService = resourceAuthorizationService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.messageSource = messageSource;
        this.emailVerificationExpirationSeconds = emailVerificationExpirationSeconds;
        this.otpExpirationSeconds = otpExpirationSeconds;
    }

    public SecurityModel createForUser(UserModel user) {
        if (securityRepository.existsByUserId(user.getId())) {
            return securityRepository.findByUserId(user.getId());
        }
        return securityRepository.save(new SecurityModel(user));
    }

    public boolean isMailVerified(UUID userId) {
        return findByUserId(userId).getIsMailVerified();
    }

    public boolean is2FAEnabled(UUID userId) {
        return findByUserId(userId).getIs2FAEnabled();
    }

    public void sendEmailVerification(UUID userId, String email) {
        SecurityModel security = findByUserId(userId);
        if (security.getIsMailVerified()) {
            return;
        }

        String rawToken = UUID.randomUUID().toString() + "." + UUID.randomUUID();
        security.setEmailVerificationTokenHash(passwordEncoder.encode(rawToken));
        security.setEmailVerificationExpiresAt(LocalDateTime.now().plusSeconds(emailVerificationExpirationSeconds));
        securityRepository.save(security);

        emailService.sendEmailVerification(email, userId.toString(), rawToken);
    }

    public VerifyEmailResult verifyEmail(UUID userId, String rawToken) {
        SecurityModel security = findByUserId(userId);
        if (security.getIsMailVerified()) {
            return VerifyEmailResult.ALREADY_VERIFIED;
        }
        if (rawToken == null || rawToken.isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "INVALID_VERIFICATION_TOKEN", "Invalid verification token");
        }
        if (security.getEmailVerificationTokenHash() == null || security.getEmailVerificationExpiresAt() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "INVALID_VERIFICATION_TOKEN", "Invalid verification token");
        }
        if (LocalDateTime.now().isAfter(security.getEmailVerificationExpiresAt())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "VERIFICATION_TOKEN_EXPIRED", "Verification token expired");
        }
        if (!passwordEncoder.matches(rawToken, security.getEmailVerificationTokenHash())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "INVALID_VERIFICATION_TOKEN", "Invalid verification token");
        }

        security.setIsMailVerified(true);
        security.setEmailVerificationTokenHash(null);
        security.setEmailVerificationExpiresAt(null);
        securityRepository.save(security);
        return VerifyEmailResult.VERIFIED;
    }

    public void generateAndSendOtp(UUID userId, String email) {
        SecurityModel security = findByUserId(userId);
        String otp = String.valueOf(100000 + RANDOM.nextInt(900000));
        security.setOTPHash(passwordEncoder.encode(otp));
        security.setOTPExpiresAt(LocalDateTime.now().plusSeconds(otpExpirationSeconds));
        securityRepository.save(security);
        emailService.sendOtpCode(email, otp);
    }

    public void verifyOtp(UUID userId, String otp) {
        SecurityModel security = findByUserId(userId);
        if (security.getOTPHash() == null || security.getOTPExpiresAt() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "OTP_NOT_REQUESTED", "OTP not requested");
        }
        if (LocalDateTime.now().isAfter(security.getOTPExpiresAt())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "OTP_EXPIRED", "OTP expired");
        }
        if (otp == null || otp.isBlank() || !passwordEncoder.matches(otp, security.getOTPHash())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "OTP_INVALID", "OTP is invalid");
        }

        security.setOTPHash(null);
        security.setOTPExpiresAt(null);
        securityRepository.save(security);
    }

    public SecurityModel setTwoFactorEnabled(boolean enabled) {
        UUID currentUserId = userCurrentId();
        SecurityModel security = findByUserId(currentUserId);
        security.setIs2FAEnabled(enabled);
        return securityRepository.save(security);
    }

    public SecurityModel me() {
        return findByUserId(userCurrentId());
    }

    private UUID userCurrentId() {
        return resourceAuthorizationService.currentUserId();
    }

    private SecurityModel findByUserId(UUID userId) {
        try {
            return securityRepository.findByUserId(userId);
        } catch (RuntimeException e) {
            throw new NotFoundException("SECURITY_NOT_FOUND", message("security.not.found", "Security settings not found."));
        }
    }

    @SuppressWarnings("null")
    private @NonNull String message(@NonNull String key, @NonNull String defaultMessage) {
        return Objects.requireNonNullElse(
            messageSource.getMessage(key, null, defaultMessage, LocaleContextHolder.getLocale()),
            defaultMessage
        );
    }

    public enum VerifyEmailResult {
        VERIFIED,
        ALREADY_VERIFIED
    }
}
