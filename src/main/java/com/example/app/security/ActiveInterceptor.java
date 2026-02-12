package com.example.app.security;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.app.exceptions.BusinessException;
import com.example.app.repositories.UserRepository;
import com.example.app.services.SecurityService;

@Component
public class ActiveInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;
    private final SecurityService securityService;

    public ActiveInterceptor(UserRepository userRepository, SecurityService securityService) {
        this.userRepository = userRepository;
        this.securityService = securityService;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        boolean requiresActive =
            handlerMethod.getMethodAnnotation(RequireActive.class) != null ||
            handlerMethod.getBeanType().getAnnotation(RequireActive.class) != null;

        if (!requiresActive) {
            return true;
        }

        Object idAttr = request.getAttribute(SecurityRequestAttributes.CURRENT_USER_ID);
        if (!(idAttr instanceof UUID userId)) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", "User is not authenticated");
        }

        if (!userRepository.isActive(userId)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "ACCOUNT_INACTIVE", "User account is inactive");
        }
        if (!securityService.isMailVerified(userId)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "EMAIL_NOT_VERIFIED", "Email is not verified");
        }

        return true;
    }
}
