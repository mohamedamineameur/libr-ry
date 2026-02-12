package com.example.app.security;

import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.app.exceptions.BusinessException;
import com.example.app.models.SessionModel;
import com.example.app.repositories.SessionRepository;
import com.example.app.repositories.UserRepository;
import com.example.app.services.TokenService;

@Service
public class ResourceAuthorizationService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final TokenService tokenService;

    public ResourceAuthorizationService(
        UserRepository userRepository,
        SessionRepository sessionRepository,
        TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.tokenService = tokenService;
    }

    public UUID currentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "MISSING_REQUEST_CONTEXT", "Missing request context");
        }

        HttpServletRequest request = attributes.getRequest();
        Object userIdAttr = request.getAttribute(SecurityRequestAttributes.CURRENT_USER_ID);
        if (userIdAttr instanceof UUID userId) {
            return userId;
        }

        String token = readTokenFromCookies(request);
        SessionModel session = requireValidSessionFromToken(token);
        return session.getUser().getId();
    }

    public UUID currentSessionId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "MISSING_REQUEST_CONTEXT", "Missing request context");
        }

        HttpServletRequest request = attributes.getRequest();
        Object sessionIdAttr = request.getAttribute(SecurityRequestAttributes.CURRENT_SESSION_ID);
        if (sessionIdAttr instanceof UUID sessionId) {
            return sessionId;
        }

        String token = readTokenFromCookies(request);
        SessionModel session = requireValidSessionFromToken(token);
        return session.getId();
    }

    public void assertOwnerOrAdmin(UUID targetOwnerId) {
        if (targetOwnerId == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "MISSING_TARGET_OWNER_ID", "Target owner id is required");
        }

        UUID currentUserId = currentUserId();
        if (currentUserId.equals(targetOwnerId)) {
            return;
        }

        boolean admin;
        try {
            admin = userRepository.isAdmin(currentUserId);
        } catch (RuntimeException e) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", "User is not authenticated");
        }

        if (!admin) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "FORBIDDEN_RESOURCE_ACCESS", "Access to this resource is forbidden");
        }
    }

    private String readTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "MISSING_TOKEN_COOKIE", "Missing token cookie");
        }

        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                String value = cookie.getValue();
                if (value == null || value.isBlank()) {
                    throw new BusinessException(HttpStatus.UNAUTHORIZED, "MISSING_TOKEN_COOKIE", "Missing token cookie");
                }
                return value;
            }
        }

        throw new BusinessException(HttpStatus.UNAUTHORIZED, "MISSING_TOKEN_COOKIE", "Missing token cookie");
    }

    private SessionModel requireValidSessionFromToken(String token) {
        UUID sessionId = tokenService.extractSessionId(token);
        SessionModel session;
        try {
            session = sessionRepository.findById(sessionId);
        } catch (RuntimeException e) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "SESSION_NOT_FOUND", "Session not found");
        }

        if (!session.getIsActive()) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "SESSION_INACTIVE", "Session is inactive");
        }
        if (session.getExpiresAt() == null || java.time.LocalDateTime.now().isAfter(session.getExpiresAt())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "SESSION_EXPIRED", "Session expired");
        }
        return session;
    }
}
