package com.example.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.app.exceptions.BusinessException;
import com.example.app.models.SecurityModel;
import com.example.app.models.UserModel;
import com.example.app.repositories.SecurityRepository;
import com.example.app.security.ResourceAuthorizationService;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Mock
    private SecurityRepository securityRepository;
    @Mock
    private ResourceAuthorizationService resourceAuthorizationService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @Mock
    private MessageSource messageSource;

    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        securityService = new SecurityService(
            securityRepository,
            resourceAuthorizationService,
            passwordEncoder,
            emailService,
            messageSource,
            3600,
            300
        );
    }

    @Test
    @DisplayName("Check that createForUser saves new security when it does not exist")
    void createForUserShouldSaveWhenMissing() {
        UUID userId = UUID.randomUUID();
        UserModel user = new UserModel("John", "john@example.com", "hashed");
        user.setId(userId);
        when(securityRepository.existsByUserId(userId)).thenReturn(false);
        when(securityRepository.save(any(SecurityModel.class))).thenAnswer(inv -> inv.getArgument(0));

        SecurityModel result = securityService.createForUser(user);

        assertEquals(userId, result.getUser().getId());
        verify(securityRepository).save(any(SecurityModel.class));
    }

    @Test
    @DisplayName("Check that verify email returns already verified when mail is already verified")
    void verifyEmailShouldReturnAlreadyVerified() {
        UUID userId = UUID.randomUUID();
        UserModel user = new UserModel("John", "john@example.com", "hashed");
        user.setId(userId);
        SecurityModel security = new SecurityModel(user);
        security.setIsMailVerified(true);
        when(securityRepository.findByUserId(userId)).thenReturn(security);

        SecurityService.VerifyEmailResult result = securityService.verifyEmail(userId, "token");

        assertEquals(SecurityService.VerifyEmailResult.ALREADY_VERIFIED, result);
    }

    @Test
    @DisplayName("Check that verify email rejects invalid token")
    void verifyEmailShouldFailWhenTokenInvalid() {
        UUID userId = UUID.randomUUID();
        UserModel user = new UserModel("John", "john@example.com", "hashed");
        user.setId(userId);
        SecurityModel security = new SecurityModel(user);
        security.setIsMailVerified(false);
        security.setEmailVerificationTokenHash("hash");
        security.setEmailVerificationExpiresAt(LocalDateTime.now().plusMinutes(2));
        when(securityRepository.findByUserId(userId)).thenReturn(security);
        when(passwordEncoder.matches("bad", "hash")).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> securityService.verifyEmail(userId, "bad"));

        assertEquals("INVALID_VERIFICATION_TOKEN", ex.getCode());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    @DisplayName("Check that verify OTP clears OTP fields when code is valid")
    void verifyOtpShouldClearOtpFieldsWhenValid() {
        UUID userId = UUID.randomUUID();
        UserModel user = new UserModel("John", "john@example.com", "hashed");
        user.setId(userId);
        SecurityModel security = new SecurityModel(user);
        security.setOTPHash("otp-hash");
        security.setOTPExpiresAt(LocalDateTime.now().plusMinutes(2));
        when(securityRepository.findByUserId(userId)).thenReturn(security);
        when(passwordEncoder.matches("123456", "otp-hash")).thenReturn(true);
        when(securityRepository.save(any(SecurityModel.class))).thenAnswer(inv -> inv.getArgument(0));

        securityService.verifyOtp(userId, "123456");

        verify(securityRepository).save(eq(security));
    }
}
