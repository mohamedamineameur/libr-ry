package com.example.app.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.example.app.config.WebMvcSecurityConfig;
import com.example.app.controllers.UserController;
import com.example.app.dtos.userDTO.UserResponse;
import com.example.app.exceptions.GlobalExceptionHandler;
import com.example.app.models.UserModel;
import com.example.app.repositories.UserRepository;
import com.example.app.security.ActiveInterceptor;
import com.example.app.security.AdminInterceptor;
import com.example.app.security.AuthenticationInterceptor;
import com.example.app.services.TokenService;
import com.example.app.services.UserService;

@WebMvcTest(controllers = UserController.class)
@Import({
    GlobalExceptionHandler.class,
    WebMvcSecurityConfig.class,
    AuthenticationInterceptor.class,
    ActiveInterceptor.class,
    AdminInterceptor.class
})
class UserSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TokenService tokenService;

    @Test
    @DisplayName("Check that /users/me returns 401 when token cookie is missing")
    void getMeShouldReturnUnauthorizedWhenTokenCookieIsMissing() throws Exception {
        mockMvc.perform(get("/users/me"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("MISSING_TOKEN_COOKIE"));
    }

    @Test
    @DisplayName("Check that /users returns 403 when authenticated user is not admin")
    void getAllUsersShouldReturnForbiddenWhenUserIsNotAdmin() throws Exception {
        UUID userId = UUID.randomUUID();
        when(tokenService.extractUserId("good-token")).thenReturn(userId);
        when(userRepository.isActive(userId)).thenReturn(true);
        when(userRepository.isAdmin(userId)).thenReturn(false);

        mockMvc.perform(get("/users").cookie(new jakarta.servlet.http.Cookie("token", "good-token")))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value("FORBIDDEN_ADMIN"));
    }

    @Test
    @DisplayName("Check that /users/me returns 403 when authenticated user is inactive")
    void getMeShouldReturnForbiddenWhenUserIsInactive() throws Exception {
        UUID userId = UUID.randomUUID();
        when(tokenService.extractUserId("good-token")).thenReturn(userId);
        when(userRepository.isActive(userId)).thenReturn(false);

        mockMvc.perform(get("/users/me").cookie(new jakarta.servlet.http.Cookie("token", "good-token")))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value("ACCOUNT_INACTIVE"));
    }

    @Test
    @DisplayName("Check that /users returns 200 when authenticated user is active admin")
    void getAllUsersShouldReturnOkWhenUserIsAdminAndActive() throws Exception {
        UUID userId = UUID.randomUUID();
        when(tokenService.extractUserId("good-token")).thenReturn(userId);
        when(userRepository.isActive(userId)).thenReturn(true);
        when(userRepository.isAdmin(userId)).thenReturn(true);

        UserModel model = new UserModel("John", "john@example.com", "hashed");
        UserResponse response = new UserResponse(model);
        when(userService.getAllUsers()).thenReturn(List.of(response));

        mockMvc.perform(get("/users").cookie(new jakarta.servlet.http.Cookie("token", "good-token")))
            .andExpect(status().isOk());
    }
}
