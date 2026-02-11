package com.example.app.exceptions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dtos.userDTO.LoginRequest;

@SuppressWarnings("null")
class GlobalExceptionHandlerTest {

    @RestController
    static class DummyController {
        @PostMapping("/dummy")
        public void endpoint(@RequestBody LoginRequest request) {
        }
    }

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        mockMvc = MockMvcBuilders
            .standaloneSetup(new DummyController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
    }

    @Test
    @DisplayName("Check that unknown JSON fields return a human readable 400 error")
    void shouldReturnUnknownFieldMessageWhenJsonContainsUnexpectedProperty() throws Exception {
        mockMvc.perform(post("/dummy")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "a@a.a",
                      "password": "x",
                      "unexpectedField": "boom"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("UNKNOWN_FIELD"))
            .andExpect(jsonPath("$.message").value("Unknown field: unexpectedField"));
    }
}
