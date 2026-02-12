package com.example.app.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.dtos.sessionDTO.SessionResponse;
import com.example.app.exceptions.BusinessException;
import com.example.app.exceptions.NotFoundException;
import com.example.app.models.SessionModel;
import com.example.app.models.UserModel;
import com.example.app.repositories.SessionRepository;
import com.example.app.security.ResourceAuthorizationService;

@Service
@Transactional
public class SessionService {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final SessionRepository sessionRepository;
    private final ResourceAuthorizationService resourceAuthorizationService;
    private final GeoIpService geoIpService;
    private final MessageSource messageSource;
    private final long sessionExpirationSeconds;

    public SessionService(
        SessionRepository sessionRepository,
        ResourceAuthorizationService resourceAuthorizationService,
        GeoIpService geoIpService,
        MessageSource messageSource,
        @Value("${session.expiration-seconds:86400}") long sessionExpirationSeconds
    ) {
        this.sessionRepository = sessionRepository;
        this.resourceAuthorizationService = resourceAuthorizationService;
        this.geoIpService = geoIpService;
        this.messageSource = messageSource;
        this.sessionExpirationSeconds = sessionExpirationSeconds;
    }

    public SessionModel createSession(UserModel user, String ipAddress, String userAgent) {
        GeoIpService.GeoIpData geoIpData = geoIpService.resolve(ipAddress);
        SessionModel session = new SessionModel(
            user,
            ipAddress,
            userAgent,
            detectBrowser(userAgent),
            detectOs(userAgent),
            geoIpData.country(),
            geoIpData.city(),
            geoIpData.region(),
            geoIpData.latitude(),
            geoIpData.longitude(),
            geoIpData.timezone()
        );
        session.setCreatedAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusSeconds(sessionExpirationSeconds));
        session.setIsActive(true);
        return sessionRepository.save(session);
    }

    public SessionModel requireValidSession(UUID sessionId) {
        SessionModel session = findSessionById(sessionId);
        if (!session.getIsActive()) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "SESSION_INACTIVE", "Session is inactive");
        }
        if (isExpired(session)) {
            sessionRepository.setActive(session.getId(), false);
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "SESSION_EXPIRED", "Session expired");
        }
        return session;
    }

    public void logoutCurrentSession() {
        UUID currentSessionId = resourceAuthorizationService.currentSessionId();
        revokeSession(currentSessionId);
    }

    public List<SessionResponse> getMySessions() {
        UUID currentUserId = resourceAuthorizationService.currentUserId();
        return sessionRepository.findByUserId(currentUserId).stream().map(SessionResponse::new).collect(Collectors.toList());
    }

    public List<SessionResponse> getMySessions(int page, int size) {
        UUID currentUserId = resourceAuthorizationService.currentUserId();
        return sessionRepository.findByUserId(currentUserId, normalizePage(page), normalizeSize(size))
            .stream()
            .map(SessionResponse::new)
            .collect(Collectors.toList());
    }

    public List<SessionResponse> getAllSessions() {
        return sessionRepository.findAll().stream().map(SessionResponse::new).collect(Collectors.toList());
    }

    public List<SessionResponse> getAllSessions(int page, int size) {
        return sessionRepository.findAll(normalizePage(page), normalizeSize(size))
            .stream()
            .map(SessionResponse::new)
            .collect(Collectors.toList());
    }

    public void revokeSession(UUID sessionId) {
        SessionModel session = findSessionById(sessionId);
        resourceAuthorizationService.assertOwnerOrAdmin(session.getUser().getId());
        sessionRepository.setActive(sessionId, false);
    }

    private SessionModel findSessionById(UUID sessionId) {
        try {
            return sessionRepository.findById(sessionId);
        } catch (RuntimeException e) {
            throw new NotFoundException("SESSION_NOT_FOUND", message("session.not.found", "Session not found."));
        }
    }

    private boolean isExpired(SessionModel session) {
        return session.getExpiresAt() == null || LocalDateTime.now().isAfter(session.getExpiresAt());
    }

    private int normalizePage(int page) {
        return Math.max(page, DEFAULT_PAGE);
    }

    private int normalizeSize(int size) {
        if (size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }

    private String detectBrowser(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }
        String ua = userAgent.toLowerCase();
        if (ua.contains("edg/")) {
            return "Edge";
        }
        if (ua.contains("chrome/")) {
            return "Chrome";
        }
        if (ua.contains("firefox/")) {
            return "Firefox";
        }
        if (ua.contains("safari/") && !ua.contains("chrome/")) {
            return "Safari";
        }
        return "Unknown";
    }

    private String detectOs(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }
        String ua = userAgent.toLowerCase();
        if (ua.contains("windows")) {
            return "Windows";
        }
        if (ua.contains("mac os")) {
            return "macOS";
        }
        if (ua.contains("linux")) {
            return "Linux";
        }
        if (ua.contains("android")) {
            return "Android";
        }
        if (ua.contains("iphone") || ua.contains("ios")) {
            return "iOS";
        }
        return "Unknown";
    }

    @SuppressWarnings("null")
    private @NonNull String message(@NonNull String key, @NonNull String defaultMessage) {
        return Objects.requireNonNullElse(
            messageSource.getMessage(key, null, defaultMessage, LocaleContextHolder.getLocale()),
            defaultMessage
        );
    }
}
