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
import com.example.app.controllers.LaonController;
import com.example.app.exceptions.GlobalExceptionHandler;
import com.example.app.repositories.UserRepository;
import com.example.app.security.ActiveInterceptor;
import com.example.app.security.AdminInterceptor;
import com.example.app.security.AuthenticationInterceptor;
import com.example.app.services.LaonService;
import com.example.app.services.TokenService;
import com.example.app.services.UserService;

@WebMvcTest(controllers = LaonController.class)
@Import({
    GlobalExceptionHandler.class,
    WebMvcSecurityConfig.class,
    AuthenticationInterceptor.class,
    ActiveInterceptor.class,
    AdminInterceptor.class
})
@SuppressWarnings("null")
class LaonSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LaonService laonService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TokenService tokenService;

    @Test
    @DisplayName("Check that reading own loans requires authentication")
    void getMineShouldReturnUnauthorizedWhenTokenCookieIsMissing() throws Exception {
        mockMvc.perform(get("/laons/me"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("MISSING_TOKEN_COOKIE"));
    }

    @Test
    @DisplayName("Check that reading own loans requires active user")
    void getMineShouldReturnForbiddenWhenUserIsInactive() throws Exception {
        UUID userId = UUID.randomUUID();
        when(tokenService.extractUserId("good-token")).thenReturn(userId);
        when(userRepository.isActive(userId)).thenReturn(false);

        mockMvc.perform(get("/laons/me").cookie(new jakarta.servlet.http.Cookie("token", "good-token")))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value("ACCOUNT_INACTIVE"));
    }

    @Test
    @DisplayName("Check that listing all loans is forbidden for non-admin users")
    void getAllShouldReturnForbiddenWhenUserIsNotAdmin() throws Exception {
        UUID userId = UUID.randomUUID();
        when(tokenService.extractUserId("good-token")).thenReturn(userId);
        when(userRepository.isActive(userId)).thenReturn(true);
        when(userRepository.isAdmin(userId)).thenReturn(false);

        mockMvc.perform(get("/laons").cookie(new jakarta.servlet.http.Cookie("token", "good-token")))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value("FORBIDDEN_ADMIN"));
    }

    @Test
    @DisplayName("Check that creating loan requires authentication")
    void createShouldReturnUnauthorizedWhenTokenCookieIsMissing() throws Exception {
        mockMvc.perform(post("/laons")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "userId": "11111111-1111-1111-1111-111111111111",
                      "bookId": "22222222-2222-2222-2222-222222222222"
                    }
                    """))
            .andExpect(status().isUnauthorized());
    }
}
