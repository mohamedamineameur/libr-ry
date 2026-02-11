package com.example.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.app.exceptions.BusinessException;

class TokenServiceTest {

    @Test
    @DisplayName("Check that generating and reading a token returns the same user id")
    void generateAndExtractTokenShouldReturnSameUserId() {
        TokenService tokenService = new TokenService("test-secret", 3600);
        UUID userId = UUID.randomUUID();

        String token = tokenService.generateToken(userId);
        UUID extracted = tokenService.extractUserId(token);

        assertEquals(userId, extracted);
    }

    @Test
    @DisplayName("Check that an invalid token format is rejected")
    void extractUserIdShouldFailWhenTokenFormatIsInvalid() {
        TokenService tokenService = new TokenService("test-secret", 3600);

        BusinessException ex = assertThrows(BusinessException.class, () -> tokenService.extractUserId("invalid-token"));
        assertEquals("INVALID_TOKEN", ex.getCode());
    }

    @Test
    @DisplayName("Check that a tampered token signature is rejected")
    void extractUserIdShouldFailWhenTokenSignatureIsInvalid() {
        TokenService tokenService = new TokenService("test-secret", 3600);
        UUID userId = UUID.randomUUID();
        String token = tokenService.generateToken(userId);
        String tampered = token + "x";

        BusinessException ex = assertThrows(BusinessException.class, () -> tokenService.extractUserId(tampered));
        assertEquals("INVALID_TOKEN_SIGNATURE", ex.getCode());
    }

    @Test
    @DisplayName("Check that an expired token is rejected")
    void extractUserIdShouldFailWhenTokenIsExpired() {
        TokenService tokenService = new TokenService("test-secret", -1);
        UUID userId = UUID.randomUUID();
        String token = tokenService.generateToken(userId);

        BusinessException ex = assertThrows(BusinessException.class, () -> tokenService.extractUserId(token));
        assertEquals("TOKEN_EXPIRED", ex.getCode());
        assertTrue(ex.getMessage().contains("expired"));
    }
}
