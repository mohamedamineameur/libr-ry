package com.example.app.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.app.config.WebMvcSecurityConfig;
import com.example.app.controllers.AuthorController;
import com.example.app.exceptions.GlobalExceptionHandler;
import com.example.app.repositories.UserRepository;
import com.example.app.security.ActiveInterceptor;
import com.example.app.security.AdminInterceptor;
import com.example.app.security.AuthenticationInterceptor;
import com.example.app.services.AuthorService;
import com.example.app.services.SecurityService;
import com.example.app.services.SessionService;
import com.example.app.services.TokenService;
import com.example.app.services.UserService;

@WebMvcTest(controllers = AuthorController.class)
@Import({
    GlobalExceptionHandler.class,
    WebMvcSecurityConfig.class,
    AuthenticationInterceptor.class,
    ActiveInterceptor.class,
    AdminInterceptor.class
})
@SuppressWarnings("null")
class AuthorSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private SessionService sessionService;
    @MockBean
    private SecurityService securityService;

    @Test
    @DisplayName("Check that reading authors requires authentication")
    void getAuthorsShouldReturnUnauthorizedWhenTokenCookieIsMissing() throws Exception {
        mockMvc.perform(get("/authors"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("MISSING_TOKEN_COOKIE"));
    }

    @Test
    @DisplayName("Check that reading authors requires active user")
    void getAuthorsShouldReturnForbiddenWhenUserIsInactive() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        when(tokenService.extractSessionId("good-token")).thenReturn(sessionId);
        com.example.app.models.UserModel user = new com.example.app.models.UserModel("John", "john@example.com", "hashed");
        user.setId(userId);
        com.example.app.models.SessionModel session = new com.example.app.models.SessionModel(
            user, "127.0.0.1", "ua", "Chrome", "Linux", null, null, null, null, null, null
        );
        session.setId(sessionId);
        session.setIsActive(true);
        session.setExpiresAt(java.time.LocalDateTime.now().plusMinutes(5));
        when(sessionService.requireValidSession(sessionId)).thenReturn(session);
        when(userRepository.isActive(userId)).thenReturn(false);

        mockMvc.perform(get("/authors").cookie(new jakarta.servlet.http.Cookie("token", "good-token")))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value("ACCOUNT_INACTIVE"));
    }

    @Test
    @DisplayName("Check that creating authors is forbidden for non-admin users")
    void createAuthorShouldReturnForbiddenWhenUserIsNotAdmin() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        when(tokenService.extractSessionId("good-token")).thenReturn(sessionId);
        com.example.app.models.UserModel user = new com.example.app.models.UserModel("John", "john@example.com", "hashed");
        user.setId(userId);
        com.example.app.models.SessionModel session = new com.example.app.models.SessionModel(
            user, "127.0.0.1", "ua", "Chrome", "Linux", null, null, null, null, null, null
        );
        session.setId(sessionId);
        session.setIsActive(true);
        session.setExpiresAt(java.time.LocalDateTime.now().plusMinutes(5));
        when(sessionService.requireValidSession(sessionId)).thenReturn(session);
        when(userRepository.isActive(userId)).thenReturn(true);
        when(securityService.isMailVerified(userId)).thenReturn(true);
        when(userRepository.isAdmin(userId)).thenReturn(false);

        mockMvc.perform(post("/authors")
                .cookie(new jakarta.servlet.http.Cookie("token", "good-token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "firstName": "Ada",
                      "lastName": "Lovelace",
                      "email": "ada@history.dev",
                      "biography": "First programmer"
                    }
                    """))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value("FORBIDDEN_ADMIN"));
    }
}
