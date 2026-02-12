package com.example.app.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

import com.example.app.dtos.authorDTO.AuthorResponse;
import com.example.app.dtos.authorDTO.CreateAuthorRequest;
import com.example.app.dtos.authorDTO.UpdateAuthorRequest;
import com.example.app.exceptions.GlobalExceptionHandler;
import com.example.app.exceptions.NotFoundException;
import com.example.app.models.AuthorModel;
import com.example.app.services.AuthorService;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class AuthorControllerTest {

    @Mock
    private AuthorService authorService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new AuthorController(authorService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    @DisplayName("Check that create author returns 201 for valid payload")
    void createAuthorShouldReturnCreated() throws Exception {
        AuthorResponse response = new AuthorResponse(new AuthorModel("Ada", "Lovelace", "ada@history.dev", "First programmer"));
        when(authorService.createAuthor(any(CreateAuthorRequest.class))).thenReturn(response);

        mockMvc.perform(post("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "firstName": "Ada",
                      "lastName": "Lovelace",
                      "email": "ada@history.dev",
                      "biography": "First programmer"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("ada@history.dev"));
    }

    @Test
    @DisplayName("Check that get all authors returns 200")
    void getAllAuthorsShouldReturnOk() throws Exception {
        AuthorResponse response = new AuthorResponse(new AuthorModel("Ada", "Lovelace", "ada@history.dev", "First programmer"));
        when(authorService.getAllAuthors(anyInt(), anyInt())).thenReturn(List.of(response));

        mockMvc.perform(get("/authors"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Check that get author by id returns 200")
    void getAuthorByIdShouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        AuthorResponse response = new AuthorResponse(new AuthorModel("Ada", "Lovelace", "ada@history.dev", "First programmer"));
        when(authorService.getAuthorById(id)).thenReturn(response);

        mockMvc.perform(get("/authors/{id}", id))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Check that update author returns 200")
    void updateAuthorShouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        AuthorResponse response = new AuthorResponse(new AuthorModel("Grace", "Hopper", "grace@history.dev", "COBOL pioneer"));
        when(authorService.updateAuthor(eq(id), any(UpdateAuthorRequest.class))).thenReturn(response);

        mockMvc.perform(put("/authors/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "firstName": "Grace",
                      "lastName": "Hopper",
                      "email": "grace@history.dev",
                      "biography": "COBOL pioneer"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Grace"));
    }

    @Test
    @DisplayName("Check that delete author returns 204")
    void deleteAuthorShouldReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/authors/{id}", id))
            .andExpect(status().isNoContent());

        verify(authorService).deleteAuthor(id);
    }

    @Test
    @DisplayName("Check that delete author returns 404 when service throws not found")
    void deleteAuthorShouldReturnNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new NotFoundException("AUTHOR_NOT_FOUND", "Author not found.")).when(authorService).deleteAuthor(id);

        mockMvc.perform(delete("/authors/{id}", id))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("AUTHOR_NOT_FOUND"));
    }
}
