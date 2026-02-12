package com.example.app.controllers;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.app.exceptions.GlobalExceptionHandler;
import com.example.app.models.SecurityModel;
import com.example.app.models.UserModel;
import com.example.app.services.SecurityService;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class SecurityControllerTest {

    @Mock
    private SecurityService securityService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new SecurityController(securityService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    @DisplayName("Check that security me returns 200")
    void meShouldReturnOk() throws Exception {
        UserModel user = new UserModel("John", "john@example.com", "hashed");
        user.setId(UUID.randomUUID());
        SecurityModel security = new SecurityModel(user);
        when(securityService.me()).thenReturn(security);

        mockMvc.perform(get("/security/me"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Check that toggling 2FA returns updated security response")
    void setTwoFactorShouldReturnOk() throws Exception {
        UserModel user = new UserModel("John", "john@example.com", "hashed");
        user.setId(UUID.randomUUID());
        SecurityModel security = new SecurityModel(user);
        security.setIs2FAEnabled(true);
        when(securityService.setTwoFactorEnabled(anyBoolean())).thenReturn(security);

        mockMvc.perform(patch("/security/2fa")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "enabled": true
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.is2FAEnabled").value(true));
    }
}
