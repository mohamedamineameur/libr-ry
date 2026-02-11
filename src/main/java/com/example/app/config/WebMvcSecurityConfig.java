package com.example.app.config;

import java.util.Objects;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.app.security.ActiveInterceptor;
import com.example.app.security.AdminInterceptor;
import com.example.app.security.AuthenticationInterceptor;

@Configuration
public class WebMvcSecurityConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;
    private final ActiveInterceptor activeInterceptor;
    private final AdminInterceptor adminInterceptor;

    public WebMvcSecurityConfig(
        AuthenticationInterceptor authenticationInterceptor,
        ActiveInterceptor activeInterceptor,
        AdminInterceptor adminInterceptor
    ) {
        this.authenticationInterceptor = authenticationInterceptor;
        this.activeInterceptor = activeInterceptor;
        this.adminInterceptor = adminInterceptor;
    }

    @Override
    @SuppressWarnings("null")
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(Objects.requireNonNull(authenticationInterceptor)).addPathPatterns("/**").order(1);
        registry.addInterceptor(Objects.requireNonNull(activeInterceptor)).addPathPatterns("/**").order(2);
        registry.addInterceptor(Objects.requireNonNull(adminInterceptor)).addPathPatterns("/**").order(3);
    }
}
