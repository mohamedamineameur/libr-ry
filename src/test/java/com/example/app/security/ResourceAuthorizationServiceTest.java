package com.example.app.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.UUID;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.app.exceptions.BusinessException;
import com.example.app.repositories.UserRepository;
import com.example.app.services.TokenService;

@ExtendWith(MockitoExtension.class)
class ResourceAuthorizationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    private ResourceAuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
        authorizationService = new ResourceAuthorizationService(userRepository, tokenService);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Check that current user id is read from request attributes when present")
    void currentUserIdShouldUseRequestAttributeWhenPresent() {
        UUID currentUser = UUID.randomUUID();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(SecurityRequestAttributes.CURRENT_USER_ID, currentUser);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        UUID result = authorizationService.currentUserId();

        assertEquals(currentUser, result);
    }

    @Test
    @DisplayName("Check that current user id falls back to token cookie when attribute is absent")
    void currentUserIdShouldFallbackToTokenCookie() {
        UUID currentUser = UUID.randomUUID();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("token", "abc.def"));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(tokenService.extractUserId("abc.def")).thenReturn(currentUser);

        UUID result = authorizationService.currentUserId();

        assertEquals(currentUser, result);
    }

    @Test
    @DisplayName("Check that owner access is allowed")
    void assertOwnerOrAdminShouldPassWhenCurrentUserIsOwner() {
        UUID currentUser = UUID.randomUUID();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(SecurityRequestAttributes.CURRENT_USER_ID, currentUser);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        assertDoesNotThrow(() -> authorizationService.assertOwnerOrAdmin(currentUser));
    }

    @Test
    @DisplayName("Check that admin access is allowed on someone else's resource")
    void assertOwnerOrAdminShouldPassWhenCurrentUserIsAdmin() {
        UUID currentUser = UUID.randomUUID();
        UUID resourceOwner = UUID.randomUUID();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(SecurityRequestAttributes.CURRENT_USER_ID, currentUser);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(userRepository.isAdmin(currentUser)).thenReturn(true);

        assertDoesNotThrow(() -> authorizationService.assertOwnerOrAdmin(resourceOwner));
    }

    @Test
    @DisplayName("Check that non owner non admin access is forbidden")
    void assertOwnerOrAdminShouldFailWhenCurrentUserIsNotOwnerAndNotAdmin() {
        UUID currentUser = UUID.randomUUID();
        UUID resourceOwner = UUID.randomUUID();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(SecurityRequestAttributes.CURRENT_USER_ID, currentUser);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(userRepository.isAdmin(currentUser)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> authorizationService.assertOwnerOrAdmin(resourceOwner));
        assertEquals("FORBIDDEN_RESOURCE_ACCESS", ex.getCode());
    }
}
