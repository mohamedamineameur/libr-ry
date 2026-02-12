package com.example.app.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
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

import com.example.app.dtos.laonDTO.CreateLaonRequest;
import com.example.app.dtos.laonDTO.LaonResponse;
import com.example.app.dtos.laonDTO.MarkLaonReturnedRequest;
import com.example.app.exceptions.GlobalExceptionHandler;
import com.example.app.models.AuthorModel;
import com.example.app.models.BookModel;
import com.example.app.models.LaonModel;
import com.example.app.models.UserModel;
import com.example.app.services.LaonService;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class LaonControllerTest {

    @Mock
    private LaonService laonService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new LaonController(laonService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    @DisplayName("Check that create loan returns 201 for valid payload")
    void createLaonShouldReturnCreated() throws Exception {
        LaonResponse response = responseFixture();
        when(laonService.createLaon(any(CreateLaonRequest.class))).thenReturn(response);

        mockMvc.perform(post("/laons")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "userId": "11111111-1111-1111-1111-111111111111",
                      "bookId": "22222222-2222-2222-2222-222222222222"
                    }
                    """))
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Check that get own loans returns 200")
    void getMineShouldReturnOk() throws Exception {
        when(laonService.getMine()).thenReturn(List.of(responseFixture()));

        mockMvc.perform(get("/laons/me"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Check that get loan by id returns 200")
    void getByIdShouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        when(laonService.getById(id)).thenReturn(responseFixture());

        mockMvc.perform(get("/laons/{id}", id))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Check that mark returned returns 200")
    void markReturnedShouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        when(laonService.markReturned(eq(id), any(MarkLaonReturnedRequest.class))).thenReturn(responseFixture());

        mockMvc.perform(patch("/laons/{id}/return", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "isReturned": true
                    }
                    """))
            .andExpect(status().isOk());
    }

    private LaonResponse responseFixture() {
        UserModel user = new UserModel("A", "a@test.dev", "pw");
        user.setId(UUID.randomUUID());
        AuthorModel author = new AuthorModel("Ada", "Lovelace", "ada@test.dev", "bio");
        author.setId(UUID.randomUUID());
        BookModel book = new BookModel("Book", "Desc", author);
        book.setId(UUID.randomUUID());
        LaonModel laon = new LaonModel(user, book);
        return new LaonResponse(laon);
    }
}
