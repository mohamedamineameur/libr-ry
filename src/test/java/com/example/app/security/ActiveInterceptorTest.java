package com.example.app.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import com.example.app.exceptions.BusinessException;
import com.example.app.repositories.UserRepository;
import com.example.app.services.SecurityService;

@ExtendWith(MockitoExtension.class)
class ActiveInterceptorTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityService securityService;

    static class DummyController {
        @RequireActive
        public void activeOnly() {
        }

        public void open() {
        }
    }

    @Test
    @DisplayName("Check that active-only route allows an active user")
    void shouldAllowWhenUserIsActive() throws Exception {
        ActiveInterceptor interceptor = new ActiveInterceptor(userRepository, securityService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerMethod handler = new HandlerMethod(new DummyController(), "activeOnly");
        UUID userId = UUID.randomUUID();
        request.setAttribute(SecurityRequestAttributes.CURRENT_USER_ID, userId);
        when(userRepository.isActive(userId)).thenReturn(true);
        when(securityService.isMailVerified(userId)).thenReturn(true);

        boolean allowed = interceptor.preHandle(request, response, handler);

        assertTrue(allowed);
    }

    @Test
    @DisplayName("Check that active-only route denies an inactive user")
    void shouldFailWhenUserIsInactive() throws Exception {
        ActiveInterceptor interceptor = new ActiveInterceptor(userRepository, securityService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerMethod handler = new HandlerMethod(new DummyController(), "activeOnly");
        UUID userId = UUID.randomUUID();
        request.setAttribute(SecurityRequestAttributes.CURRENT_USER_ID, userId);
        when(userRepository.isActive(userId)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> interceptor.preHandle(request, response, handler));
        assertEquals("ACCOUNT_INACTIVE", ex.getCode());
    }

    @Test
    @DisplayName("Check that active-only route denies unauthenticated access")
    void shouldFailWhenUnauthenticated() throws Exception {
        ActiveInterceptor interceptor = new ActiveInterceptor(userRepository, securityService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerMethod handler = new HandlerMethod(new DummyController(), "activeOnly");

        BusinessException ex = assertThrows(BusinessException.class, () -> interceptor.preHandle(request, response, handler));
        assertEquals("UNAUTHENTICATED", ex.getCode());
    }

    @Test
    @DisplayName("Check that active-only route denies user when email is not verified")
    void shouldFailWhenEmailIsNotVerified() throws Exception {
        ActiveInterceptor interceptor = new ActiveInterceptor(userRepository, securityService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerMethod handler = new HandlerMethod(new DummyController(), "activeOnly");
        UUID userId = UUID.randomUUID();
        request.setAttribute(SecurityRequestAttributes.CURRENT_USER_ID, userId);
        when(userRepository.isActive(userId)).thenReturn(true);
        when(securityService.isMailVerified(userId)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> interceptor.preHandle(request, response, handler));
        assertEquals("EMAIL_NOT_VERIFIED", ex.getCode());
    }
}
