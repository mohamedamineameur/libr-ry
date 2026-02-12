package com.example.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.example.app.exceptions.BusinessException;
import com.example.app.models.SessionModel;
import com.example.app.models.UserModel;
import com.example.app.repositories.SessionRepository;
import com.example.app.security.ResourceAuthorizationService;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private ResourceAuthorizationService resourceAuthorizationService;
    @Mock
    private GeoIpService geoIpService;
    @Mock
    private MessageSource messageSource;

    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService(sessionRepository, resourceAuthorizationService, geoIpService, messageSource, 3600);
    }

    @Test
    @DisplayName("Check that valid session is accepted by authentication flow")
    void requireValidSessionShouldPassWhenSessionIsActiveAndNotExpired() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        UserModel user = new UserModel("John", "john@example.com", "hashed");
        user.setId(userId);
        SessionModel session = new SessionModel(user, "127.0.0.1", "ua", "Chrome", "Linux", null, null, null, null, null, null);
        session.setId(sessionId);
        session.setIsActive(true);
        session.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        when(sessionRepository.findById(sessionId)).thenReturn(session);

        SessionModel result = sessionService.requireValidSession(sessionId);

        assertEquals(sessionId, result.getId());
    }

    @Test
    @DisplayName("Check that inactive session is rejected by authentication flow")
    void requireValidSessionShouldFailWhenSessionIsInactive() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        UserModel user = new UserModel("John", "john@example.com", "hashed");
        user.setId(userId);
        SessionModel session = new SessionModel(user, "127.0.0.1", "ua", "Chrome", "Linux", null, null, null, null, null, null);
        session.setId(sessionId);
        session.setIsActive(false);
        session.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        when(sessionRepository.findById(sessionId)).thenReturn(session);

        BusinessException ex = assertThrows(BusinessException.class, () -> sessionService.requireValidSession(sessionId));

        assertEquals("SESSION_INACTIVE", ex.getCode());
    }

    @Test
    @DisplayName("Check that logout revokes the current session")
    void logoutCurrentSessionShouldSetSessionInactive() {
        UUID sessionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserModel user = new UserModel("John", "john@example.com", "hashed");
        user.setId(userId);
        SessionModel session = new SessionModel(user, "127.0.0.1", "ua", "Chrome", "Linux", null, null, null, null, null, null);
        session.setId(sessionId);
        when(resourceAuthorizationService.currentSessionId()).thenReturn(sessionId);
        when(sessionRepository.findById(sessionId)).thenReturn(session);

        sessionService.logoutCurrentSession();

        verify(sessionRepository).setActive(sessionId, false);
    }
}
