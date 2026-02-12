package com.example.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import com.example.app.dtos.laonDTO.CreateLaonRequest;
import com.example.app.dtos.laonDTO.LaonResponse;
import com.example.app.dtos.laonDTO.MarkLaonReturnedRequest;
import com.example.app.exceptions.BusinessException;
import com.example.app.models.AuthorModel;
import com.example.app.models.BookModel;
import com.example.app.models.LaonModel;
import com.example.app.models.UserModel;
import com.example.app.repositories.BookRepository;
import com.example.app.repositories.LaonRepository;
import com.example.app.repositories.UserRepository;
import com.example.app.security.ResourceAuthorizationService;

@ExtendWith(MockitoExtension.class)
class LaonServiceTest {

    @Mock
    private LaonRepository laonRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ResourceAuthorizationService resourceAuthorizationService;

    @Mock
    private MessageSource messageSource;

    private LaonService laonService;

    @BeforeEach
    void setUp() {
        laonService = new LaonService(laonRepository, userRepository, bookRepository, resourceAuthorizationService, messageSource);
    }

    @Test
    @DisplayName("Check that create loan enforces owner-or-admin on target user id")
    void createLaonShouldEnforceOwnerOrAdmin() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        CreateLaonRequest request = new CreateLaonRequest();
        request.setUserId(userId);
        request.setBookId(bookId);

        UserModel user = new UserModel("A", "a@test.dev", "pw");
        user.setId(userId);
        AuthorModel author = new AuthorModel("Ada", "Lovelace", "ada@test.dev", "bio");
        author.setId(UUID.randomUUID());
        BookModel book = new BookModel("B", "Desc", author);
        book.setId(bookId);
        LaonModel loan = new LaonModel(user, book);

        when(userRepository.findById(userId)).thenReturn(user);
        when(bookRepository.findById(bookId)).thenReturn(book);
        when(laonRepository.findByBookId(bookId)).thenThrow(new RuntimeException("Loan not found"));
        when(laonRepository.save(any(LaonModel.class))).thenReturn(loan);

        LaonResponse response = laonService.createLaon(request);

        assertNotNull(response);
        verify(resourceAuthorizationService).assertOwnerOrAdmin(userId);
    }

    @Test
    @DisplayName("Check that create loan fails when book already has active loan")
    void createLaonShouldFailWhenBookAlreadyLoaned() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        CreateLaonRequest request = new CreateLaonRequest();
        request.setUserId(userId);
        request.setBookId(bookId);

        UserModel user = new UserModel("A", "a@test.dev", "pw");
        user.setId(userId);
        AuthorModel author = new AuthorModel("Ada", "Lovelace", "ada@test.dev", "bio");
        author.setId(UUID.randomUUID());
        BookModel book = new BookModel("B", "Desc", author);
        book.setId(bookId);
        LaonModel existing = new LaonModel(user, book);
        existing.setIsReturned(false);

        when(userRepository.findById(userId)).thenReturn(user);
        when(bookRepository.findById(bookId)).thenReturn(book);
        when(laonRepository.findByBookId(bookId)).thenReturn(existing);

        BusinessException ex = assertThrows(BusinessException.class, () -> laonService.createLaon(request));
        assertEquals("BOOK_ALREADY_LOANED", ex.getCode());
    }

    @Test
    @DisplayName("Check that mark returned enforces owner-or-admin on loan owner")
    void markReturnedShouldEnforceOwnerOrAdmin() {
        UUID loanId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        UserModel user = new UserModel("A", "a@test.dev", "pw");
        user.setId(userId);
        AuthorModel author = new AuthorModel("Ada", "Lovelace", "ada@test.dev", "bio");
        author.setId(UUID.randomUUID());
        BookModel book = new BookModel("B", "Desc", author);
        book.setId(bookId);
        LaonModel existing = new LaonModel(user, book);
        existing.setIsReturned(false);

        MarkLaonReturnedRequest request = new MarkLaonReturnedRequest();
        request.setIsReturned(true);

        when(laonRepository.findById(loanId)).thenReturn(existing);
        when(laonRepository.updateReturning(loanId, existing)).thenReturn(existing);

        LaonResponse response = laonService.markReturned(loanId, request);

        assertNotNull(response);
        verify(resourceAuthorizationService).assertOwnerOrAdmin(userId);
    }
}
