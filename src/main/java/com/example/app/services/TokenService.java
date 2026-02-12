package com.example.app.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.app.exceptions.BusinessException;

@Service
public class TokenService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final String secret;
    private final long expirationSeconds;
    private final long challengeExpirationSeconds;
    private final Base64.Encoder urlEncoder = Base64.getUrlEncoder().withoutPadding();
    private final Base64.Decoder urlDecoder = Base64.getUrlDecoder();

    public TokenService(
        @Value("${token.secret}") String secret,
        @Value("${token.expiration-seconds:86400}") long expirationSeconds,
        @Value("${token.challenge-expiration-seconds:300}") long challengeExpirationSeconds
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("token.secret must be configured");
        }
        this.secret = secret;
        this.expirationSeconds = expirationSeconds;
        this.challengeExpirationSeconds = challengeExpirationSeconds;
    }

    public String generateToken(UUID sessionId) {
        return generateSignedToken(sessionId, expirationSeconds);
    }

    public String generateLoginChallengeToken(UUID userId) {
        return generateSignedToken(userId, challengeExpirationSeconds);
    }

    public UUID extractSessionId(String token) {
        return extractSignedUuid(token, "session");
    }

    public UUID extractLoginChallengeUserId(String token) {
        return extractSignedUuid(token, "login challenge");
    }

    private String generateSignedToken(UUID id, long seconds) {
        long exp = Instant.now().plusSeconds(seconds).getEpochSecond();
        String payload = id + ":" + exp;
        String encodedPayload = urlEncoder.encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        String signature = sign(encodedPayload);
        return encodedPayload + "." + signature;
    }

    private UUID extractSignedUuid(String token, String tokenType) {
        if (token == null || token.isBlank()) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Invalid " + tokenType + " token");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 2) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Invalid token format");
        }

        String encodedPayload = parts[0];
        String receivedSignature = parts[1];
        String expectedSignature = sign(encodedPayload);

        if (!MessageDigest.isEqual(
            receivedSignature.getBytes(StandardCharsets.UTF_8),
            expectedSignature.getBytes(StandardCharsets.UTF_8)
        )) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN_SIGNATURE", "Invalid token signature");
        }

        String payload;
        try {
            payload = new String(urlDecoder.decode(encodedPayload), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Invalid token payload");
        }

        String[] payloadParts = payload.split(":");
        if (payloadParts.length != 2) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Invalid token payload");
        }

        UUID sessionId;
        long exp;
        try {
            sessionId = UUID.fromString(payloadParts[0]);
            exp = Long.parseLong(payloadParts[1]);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Invalid token payload");
        }

        if (Instant.now().getEpochSecond() > exp) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "Token expired");
        }

        return sessionId;
    }

    private String sign(String encodedPayload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] signature = mac.doFinal(encodedPayload.getBytes(StandardCharsets.UTF_8));
            return urlEncoder.encodeToString(signature);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to sign token", e);
        }
    }
}
