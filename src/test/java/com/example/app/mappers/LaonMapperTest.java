package com.example.app.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.app.infrastructure.entities.AuthorEntity;
import com.example.app.infrastructure.entities.BookEntity;
import com.example.app.infrastructure.entities.LaonEntity;
import com.example.app.infrastructure.entities.UserEntity;
import com.example.app.models.LaonModel;

class LaonMapperTest {

    @Test
    @DisplayName("Check that mapper converts loan entity to domain with key fields")
    void toDomainShouldMapImportantFields() {
        UUID userId = UUID.randomUUID();
        UserEntity userEntity = mock(UserEntity.class);
        when(userEntity.getId()).thenReturn(userId);
        when(userEntity.getName()).thenReturn("A");
        when(userEntity.getEmail()).thenReturn("a@test.dev");
        when(userEntity.getPassword()).thenReturn("pw");

        AuthorEntity authorEntity = mock(AuthorEntity.class);
        when(authorEntity.getId()).thenReturn(UUID.randomUUID());
        when(authorEntity.getFirstName()).thenReturn("Ada");
        when(authorEntity.getLastName()).thenReturn("Lovelace");
        when(authorEntity.getEmail()).thenReturn("ada@test.dev");
        when(authorEntity.getBiography()).thenReturn("bio");

        UUID bookId = UUID.randomUUID();
        BookEntity bookEntity = mock(BookEntity.class);
        when(bookEntity.getId()).thenReturn(bookId);
        when(bookEntity.getTitle()).thenReturn("Book");
        when(bookEntity.getDescription()).thenReturn("Desc");
        when(bookEntity.getAuthor()).thenReturn(authorEntity);

        UUID loanId = UUID.randomUUID();
        LaonEntity loanEntity = mock(LaonEntity.class);
        when(loanEntity.getId()).thenReturn(loanId);
        when(loanEntity.getUser()).thenReturn(userEntity);
        when(loanEntity.getBook()).thenReturn(bookEntity);
        when(loanEntity.getLoanDate()).thenReturn(java.time.LocalDateTime.now());
        when(loanEntity.getReturnDate()).thenReturn(java.time.LocalDateTime.now().plusDays(30));
        when(loanEntity.getIsReturned()).thenReturn(false);

        LaonModel model = LaonMapper.toDomain(loanEntity);

        assertNotNull(model.getId());
        assertEquals(loanId, model.getId());
        assertEquals(userId, model.getUser().getId());
        assertEquals(bookId, model.getBook().getId());
    }
}
