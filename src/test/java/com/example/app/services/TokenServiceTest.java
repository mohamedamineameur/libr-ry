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
    @DisplayName("Check that generating and reading a token returns the same session id")
    void generateAndExtractTokenShouldReturnSameSessionId() {
        TokenService tokenService = new TokenService("test-secret", 3600, 300);
        UUID sessionId = UUID.randomUUID();

        String token = tokenService.generateToken(sessionId);
        UUID extracted = tokenService.extractSessionId(token);

        assertEquals(sessionId, extracted);
    }

    @Test
    @DisplayName("Check that an invalid token format is rejected")
    void extractSessionIdShouldFailWhenTokenFormatIsInvalid() {
        TokenService tokenService = new TokenService("test-secret", 3600, 300);

        BusinessException ex = assertThrows(BusinessException.class, () -> tokenService.extractSessionId("invalid-token"));
        assertEquals("INVALID_TOKEN", ex.getCode());
    }

    @Test
    @DisplayName("Check that a tampered token signature is rejected")
    void extractSessionIdShouldFailWhenTokenSignatureIsInvalid() {
        TokenService tokenService = new TokenService("test-secret", 3600, 300);
        UUID sessionId = UUID.randomUUID();
        String token = tokenService.generateToken(sessionId);
        String tampered = token + "x";

        BusinessException ex = assertThrows(BusinessException.class, () -> tokenService.extractSessionId(tampered));
        assertEquals("INVALID_TOKEN_SIGNATURE", ex.getCode());
    }

    @Test
    @DisplayName("Check that an expired token is rejected")
    void extractSessionIdShouldFailWhenTokenIsExpired() {
        TokenService tokenService = new TokenService("test-secret", -1, 300);
        UUID sessionId = UUID.randomUUID();
        String token = tokenService.generateToken(sessionId);

        BusinessException ex = assertThrows(BusinessException.class, () -> tokenService.extractSessionId(token));
        assertEquals("TOKEN_EXPIRED", ex.getCode());
        assertTrue(ex.getMessage().contains("expired"));
    }
}
