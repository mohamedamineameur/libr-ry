package com.example.app.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.app.dtos.sessionDTO.SessionResponse;
import com.example.app.exceptions.GlobalExceptionHandler;
import com.example.app.models.SessionModel;
import com.example.app.models.UserModel;
import com.example.app.services.SessionService;

@ExtendWith(MockitoExtension.class)
class SessionControllerTest {

    @Mock
    private SessionService sessionService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new SessionController(sessionService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    @DisplayName("Check that listing my sessions returns 200")
    void getMySessionsShouldReturnOk() throws Exception {
        UserModel user = new UserModel("John", "john@example.com", "hashed");
        user.setId(UUID.randomUUID());
        SessionModel session = new SessionModel(user, "127.0.0.1", "ua", "Chrome", "Linux", null, null, null, null, null, null);
        SessionResponse response = new SessionResponse(session);
        when(sessionService.getMySessions()).thenReturn(List.of(response));

        mockMvc.perform(get("/sessions/me"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Check that admin listing all sessions returns 200")
    void getAllSessionsShouldReturnOk() throws Exception {
        when(sessionService.getAllSessions()).thenReturn(List.of());

        mockMvc.perform(get("/sessions"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Check that revoking a session returns 204")
    void revokeSessionShouldReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(patch("/sessions/{id}/revoke", id))
            .andExpect(status().isNoContent());

        verify(sessionService).revokeSession(id);
    }
}
