package com.example.app.security;

import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.app.exceptions.BusinessException;
import com.example.app.models.SessionModel;
import com.example.app.services.SessionService;
import com.example.app.services.TokenService;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    private final SessionService sessionService;

    public AuthenticationInterceptor(TokenService tokenService, SessionService sessionService) {
        this.tokenService = tokenService;
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        boolean requiresAuth =
            handlerMethod.getMethodAnnotation(RequireAuthenticated.class) != null ||
            handlerMethod.getBeanType().getAnnotation(RequireAuthenticated.class) != null ||
            handlerMethod.getMethodAnnotation(RequireAdmin.class) != null ||
            handlerMethod.getBeanType().getAnnotation(RequireAdmin.class) != null ||
            handlerMethod.getMethodAnnotation(RequireActive.class) != null ||
            handlerMethod.getBeanType().getAnnotation(RequireActive.class) != null;

        if (!requiresAuth) {
            return true;
        }

        String token = readCookieValue(request, "token");
        UUID sessionId = tokenService.extractSessionId(token);
        SessionModel session = sessionService.requireValidSession(sessionId);
        request.setAttribute(SecurityRequestAttributes.CURRENT_SESSION_ID, session.getId());
        request.setAttribute(SecurityRequestAttributes.CURRENT_USER_ID, session.getUser().getId());
        return true;
    }

    private String readCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "MISSING_TOKEN_COOKIE", "Missing token cookie");
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                String value = cookie.getValue();
                if (value == null || value.isBlank()) {
                    throw new BusinessException(HttpStatus.UNAUTHORIZED, "MISSING_TOKEN_COOKIE", "Missing token cookie");
                }
                return value;
            }
        }

        throw new BusinessException(HttpStatus.UNAUTHORIZED, "MISSING_TOKEN_COOKIE", "Missing token cookie");
    }
}
