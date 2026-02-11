package com.example.app.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.UUID;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import com.example.app.exceptions.BusinessException;
import com.example.app.services.TokenService;

@ExtendWith(MockitoExtension.class)
class AuthenticationInterceptorTest {

    @Mock
    private TokenService tokenService;

    static class DummyController {
        @RequireAuthenticated
        public void secured() {
        }

        public void open() {
        }
    }

    @Test
    @DisplayName("Check that authenticated routes store current user id in request attributes")
    void shouldSetCurrentUserIdForSecuredHandler() throws Exception {
        AuthenticationInterceptor interceptor = new AuthenticationInterceptor(tokenService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("token", "abc.def"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerMethod handler = new HandlerMethod(new DummyController(), "secured");
        UUID userId = UUID.randomUUID();
        when(tokenService.extractUserId("abc.def")).thenReturn(userId);

        boolean allowed = interceptor.preHandle(request, response, handler);

        assertTrue(allowed);
        assertEquals(userId, request.getAttribute(SecurityRequestAttributes.CURRENT_USER_ID));
    }

    @Test
    @DisplayName("Check that open routes bypass authentication interceptor")
    void shouldSkipAuthenticationForOpenHandler() throws Exception {
        AuthenticationInterceptor interceptor = new AuthenticationInterceptor(tokenService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerMethod handler = new HandlerMethod(new DummyController(), "open");

        boolean allowed = interceptor.preHandle(request, response, handler);

        assertTrue(allowed);
        assertNull(request.getAttribute(SecurityRequestAttributes.CURRENT_USER_ID));
    }

    @Test
    @DisplayName("Check that secured routes fail when token cookie is missing")
    void shouldFailWhenTokenCookieIsMissingOnSecuredHandler() throws Exception {
        AuthenticationInterceptor interceptor = new AuthenticationInterceptor(tokenService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerMethod handler = new HandlerMethod(new DummyController(), "secured");

        BusinessException ex = assertThrows(BusinessException.class, () -> interceptor.preHandle(request, response, handler));
        assertEquals("MISSING_TOKEN_COOKIE", ex.getCode());
    }
}
