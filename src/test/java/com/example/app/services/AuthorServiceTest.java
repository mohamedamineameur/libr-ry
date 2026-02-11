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
import org.springframework.http.HttpStatus;

import com.example.app.dtos.authorDTO.AuthorResponse;
import com.example.app.dtos.authorDTO.CreateAuthorRequest;
import com.example.app.dtos.authorDTO.UpdateAuthorRequest;
import com.example.app.exceptions.BusinessException;
import com.example.app.exceptions.NotFoundException;
import com.example.app.models.AuthorModel;
import com.example.app.repositories.AuthorRepository;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private MessageSource messageSource;

    private AuthorService authorService;

    @BeforeEach
    void setUp() {
        authorService = new AuthorService(authorRepository, messageSource);
    }

    @Test
    @DisplayName("Check that creating an author fails when email already exists")
    void createAuthorShouldFailWhenEmailAlreadyExists() {
        CreateAuthorRequest request = new CreateAuthorRequest();
        request.setFirstName("Ada");
        request.setLastName("Lovelace");
        request.setEmail("ada@history.dev");
        request.setBiography("First programmer");
        when(authorRepository.existsByEmail("ada@history.dev")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> authorService.createAuthor(request));
        assertEquals("AUTHOR_EMAIL_ALREADY_EXISTS", ex.getCode());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    @DisplayName("Check that creating an author succeeds with valid payload")
    void createAuthorShouldSucceed() {
        CreateAuthorRequest request = new CreateAuthorRequest();
        request.setFirstName("Ada");
        request.setLastName("Lovelace");
        request.setEmail("ada@history.dev");
        request.setBiography("First programmer");

        AuthorModel saved = new AuthorModel("Ada", "Lovelace", "ada@history.dev", "First programmer");
        when(authorRepository.existsByEmail("ada@history.dev")).thenReturn(false);
        when(authorRepository.save(any(AuthorModel.class))).thenReturn(saved);

        AuthorResponse response = authorService.createAuthor(request);

        assertNotNull(response);
        assertEquals("Ada", response.getFirstName());
    }

    @Test
    @DisplayName("Check that update author fails when target author does not exist")
    void updateAuthorShouldFailWhenAuthorNotFound() {
        UUID id = UUID.randomUUID();
        UpdateAuthorRequest request = new UpdateAuthorRequest();
        request.setFirstName("Grace");
        when(authorRepository.findById(id)).thenThrow(new RuntimeException("Author not found"));

        NotFoundException ex = assertThrows(NotFoundException.class, () -> authorService.updateAuthor(id, request));
        assertEquals("AUTHOR_NOT_FOUND", ex.getCode());
    }
}
