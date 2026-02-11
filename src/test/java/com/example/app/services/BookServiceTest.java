package com.example.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import com.example.app.dtos.bookDTO.BookResponse;
import com.example.app.dtos.bookDTO.CreateBookRequest;
import com.example.app.dtos.bookDTO.UpdateBookRequest;
import com.example.app.exceptions.NotFoundException;
import com.example.app.models.AuthorModel;
import com.example.app.models.BookModel;
import com.example.app.repositories.AuthorRepository;
import com.example.app.repositories.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private MessageSource messageSource;

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService(bookRepository, authorRepository, messageSource);
    }

    @Test
    @DisplayName("Check that creating a book fails when author does not exist")
    void createBookShouldFailWhenAuthorNotFound() {
        UUID authorId = UUID.randomUUID();
        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("Computing Foundations");
        request.setDescription("History of computing");
        request.setAuthorId(authorId);
        when(authorRepository.findById(authorId)).thenThrow(new RuntimeException("Author not found"));

        NotFoundException ex = assertThrows(NotFoundException.class, () -> bookService.createBook(request));
        assertEquals("AUTHOR_NOT_FOUND", ex.getCode());
    }

    @Test
    @DisplayName("Check that creating a book succeeds with valid payload")
    void createBookShouldSucceed() {
        UUID authorId = UUID.randomUUID();
        AuthorModel author = new AuthorModel("Ada", "Lovelace", "ada@history.dev", "First programmer");
        author.setId(authorId);

        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("Computing Foundations");
        request.setDescription("History of computing");
        request.setAuthorId(authorId);

        BookModel saved = new BookModel("Computing Foundations", "History of computing", author);
        when(authorRepository.findById(authorId)).thenReturn(author);
        when(bookRepository.save(any(BookModel.class))).thenReturn(saved);

        BookResponse response = bookService.createBook(request);

        assertNotNull(response);
        assertEquals("Computing Foundations", response.getTitle());
    }

    @Test
    @DisplayName("Check that update book fails when target book does not exist")
    void updateBookShouldFailWhenBookNotFound() {
        UUID id = UUID.randomUUID();
        UpdateBookRequest request = new UpdateBookRequest();
        request.setTitle("Updated title");
        when(bookRepository.findById(id)).thenThrow(new RuntimeException("Book not found"));

        NotFoundException ex = assertThrows(NotFoundException.class, () -> bookService.updateBook(id, request));
        assertEquals("BOOK_NOT_FOUND", ex.getCode());
    }
}
