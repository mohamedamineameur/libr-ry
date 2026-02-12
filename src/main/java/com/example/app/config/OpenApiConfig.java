package com.example.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI libraryOpenApi() {
        return new OpenAPI().info(
            new Info()
                .title("Library API")
                .description("Enterprise-ready API documentation for users, authors, books, loans, sessions, and security flows.")
                .version("v1")
                .contact(new Contact().name("Library API Team"))
        );
    }
}
