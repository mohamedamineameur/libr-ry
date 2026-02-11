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

@ExtendWith(MockitoExtension.class)
class AdminInterceptorTest {

    @Mock
    private UserRepository userRepository;

    static class DummyController {
        @RequireAdmin
        public void adminOnly() {
        }

        public void open() {
        }
    }

    @Test
    @DisplayName("Check that admin-only route allows an admin user")
    void shouldAllowWhenUserIsAdmin() throws Exception {
        AdminInterceptor interceptor = new AdminInterceptor(userRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerMethod handler = new HandlerMethod(new DummyController(), "adminOnly");
        UUID userId = UUID.randomUUID();
        request.setAttribute(SecurityRequestAttributes.CURRENT_USER_ID, userId);
        when(userRepository.isAdmin(userId)).thenReturn(true);

        boolean allowed = interceptor.preHandle(request, response, handler);

        assertTrue(allowed);
    }

    @Test
    @DisplayName("Check that admin-only route denies a non-admin user")
    void shouldFailWhenUserIsNotAdmin() throws Exception {
        AdminInterceptor interceptor = new AdminInterceptor(userRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerMethod handler = new HandlerMethod(new DummyController(), "adminOnly");
        UUID userId = UUID.randomUUID();
        request.setAttribute(SecurityRequestAttributes.CURRENT_USER_ID, userId);
        when(userRepository.isAdmin(userId)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> interceptor.preHandle(request, response, handler));
        assertEquals("FORBIDDEN_ADMIN", ex.getCode());
    }

    @Test
    @DisplayName("Check that admin-only route denies unauthenticated access")
    void shouldFailWhenUnauthenticated() throws Exception {
        AdminInterceptor interceptor = new AdminInterceptor(userRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerMethod handler = new HandlerMethod(new DummyController(), "adminOnly");

        BusinessException ex = assertThrows(BusinessException.class, () -> interceptor.preHandle(request, response, handler));
        assertEquals("UNAUTHENTICATED", ex.getCode());
    }
}
