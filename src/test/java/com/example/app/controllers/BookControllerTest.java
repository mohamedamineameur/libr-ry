package com.example.app.controllers;

import static org.mockito.ArgumentMatchers.any;
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

import com.example.app.dtos.bookDTO.BookResponse;
import com.example.app.dtos.bookDTO.CreateBookRequest;
import com.example.app.dtos.bookDTO.UpdateBookRequest;
import com.example.app.exceptions.GlobalExceptionHandler;
import com.example.app.exceptions.NotFoundException;
import com.example.app.models.AuthorModel;
import com.example.app.models.BookModel;
import com.example.app.services.BookService;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class BookControllerTest {

    @Mock
    private BookService bookService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new BookController(bookService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    @DisplayName("Check that create book returns 201 for valid payload")
    void createBookShouldReturnCreated() throws Exception {
        AuthorModel author = new AuthorModel("Ada", "Lovelace", "ada@history.dev", "First programmer");
        author.setId(UUID.randomUUID());
        BookResponse response = new BookResponse(new BookModel("Computing Foundations", "History of computing", author));
        when(bookService.createBook(any(CreateBookRequest.class))).thenReturn(response);

        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "Computing Foundations",
                      "description": "History of computing",
                      "authorId": "11111111-1111-1111-1111-111111111111"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Computing Foundations"));
    }

    @Test
    @DisplayName("Check that get all books returns 200")
    void getAllBooksShouldReturnOk() throws Exception {
        AuthorModel author = new AuthorModel("Ada", "Lovelace", "ada@history.dev", "First programmer");
        author.setId(UUID.randomUUID());
        BookResponse response = new BookResponse(new BookModel("Computing Foundations", "History of computing", author));
        when(bookService.getAllBooks()).thenReturn(List.of(response));

        mockMvc.perform(get("/books"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Check that get book by id returns 200")
    void getBookByIdShouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        AuthorModel author = new AuthorModel("Ada", "Lovelace", "ada@history.dev", "First programmer");
        author.setId(UUID.randomUUID());
        BookResponse response = new BookResponse(new BookModel("Computing Foundations", "History of computing", author));
        when(bookService.getBookById(id)).thenReturn(response);

        mockMvc.perform(get("/books/{id}", id))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Check that update book returns 200")
    void updateBookShouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        AuthorModel author = new AuthorModel("Ada", "Lovelace", "ada@history.dev", "First programmer");
        author.setId(UUID.randomUUID());
        BookResponse response = new BookResponse(new BookModel("Updated title", "Updated description", author));
        when(bookService.updateBook(eq(id), any(UpdateBookRequest.class))).thenReturn(response);

        mockMvc.perform(put("/books/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "Updated title",
                      "description": "Updated description"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated title"));
    }

    @Test
    @DisplayName("Check that delete book returns 204")
    void deleteBookShouldReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/books/{id}", id))
            .andExpect(status().isNoContent());

        verify(bookService).deleteBook(id);
    }

    @Test
    @DisplayName("Check that delete book returns 404 when service throws not found")
    void deleteBookShouldReturnNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new NotFoundException("BOOK_NOT_FOUND", "Book not found.")).when(bookService).deleteBook(id);

        mockMvc.perform(delete("/books/{id}", id))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("BOOK_NOT_FOUND"));
    }
}
