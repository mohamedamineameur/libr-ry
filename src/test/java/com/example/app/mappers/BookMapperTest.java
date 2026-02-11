package com.example.app.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.app.infrastructure.entities.AuthorEntity;
import com.example.app.infrastructure.entities.BookEntity;
import com.example.app.models.AuthorModel;
import com.example.app.models.BookModel;

class BookMapperTest {

    @Test
    @DisplayName("Check that mapper converts book entity to domain with key fields")
    void toDomainShouldMapImportantFields() {
        UUID authorId = UUID.randomUUID();
        AuthorEntity authorEntity = mock(AuthorEntity.class);
        when(authorEntity.getId()).thenReturn(authorId);
        when(authorEntity.getFirstName()).thenReturn("Ada");
        when(authorEntity.getLastName()).thenReturn("Lovelace");
        when(authorEntity.getEmail()).thenReturn("ada@history.dev");
        when(authorEntity.getBiography()).thenReturn("First programmer");

        UUID bookId = UUID.randomUUID();
        BookEntity bookEntity = mock(BookEntity.class);
        when(bookEntity.getId()).thenReturn(bookId);
        when(bookEntity.getTitle()).thenReturn("Computing Foundations");
        when(bookEntity.getDescription()).thenReturn("History of computing");
        when(bookEntity.getAuthor()).thenReturn(authorEntity);
        when(bookEntity.getPublishedAt()).thenReturn(LocalDateTime.now());
        when(bookEntity.getUpdatedAt()).thenReturn(LocalDateTime.now());

        BookModel model = BookMapper.toDomain(bookEntity);

        assertNotNull(model.getId());
        assertEquals(bookId, model.getId());
        assertEquals("Computing Foundations", model.getTitle());
        assertEquals(authorId, model.getAuthor().getId());
    }

    @Test
    @DisplayName("Check that mapper converts book domain to entity with key fields")
    void toEntityShouldMapImportantFields() {
        AuthorModel author = new AuthorModel("Ada", "Lovelace", "ada@history.dev", "First programmer");
        BookModel model = new BookModel("Computing Foundations", "History of computing", author);
        AuthorEntity authorEntity = new AuthorEntity("Ada", "Lovelace", "ada@history.dev", "First programmer");

        BookEntity entity = BookMapper.toEntity(model, authorEntity);

        assertEquals("Computing Foundations", entity.getTitle());
        assertEquals("History of computing", entity.getDescription());
        assertNotNull(entity.getAuthor());
    }
}
