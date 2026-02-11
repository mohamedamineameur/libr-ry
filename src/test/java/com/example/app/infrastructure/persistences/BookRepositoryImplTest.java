package com.example.app.infrastructure.persistences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.app.infrastructure.entities.AuthorEntity;
import com.example.app.infrastructure.entities.BookEntity;
import com.example.app.models.AuthorModel;
import com.example.app.models.BookModel;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class BookRepositoryImplTest {

    @Mock
    private BookRepositoryJpa bookRepositoryJpa;

    @Mock
    private AuthorRepositoryJpa authorRepositoryJpa;

    @Test
    @DisplayName("Check that repository persists a new book with linked author")
    void saveShouldPersistBook() {
        BookRepositoryImpl repository = new BookRepositoryImpl(bookRepositoryJpa, authorRepositoryJpa);
        UUID authorId = UUID.randomUUID();

        AuthorModel authorModel = new AuthorModel("Ada", "Lovelace", "ada@history.dev", "First programmer");
        authorModel.setId(authorId);
        BookModel book = new BookModel("Computing Foundations", "History of computing", authorModel);

        AuthorEntity authorEntity = new AuthorEntity("Ada", "Lovelace", "ada@history.dev", "First programmer");
        BookEntity persisted = new BookEntity("Computing Foundations", "History of computing", authorEntity);

        when(authorRepositoryJpa.findById(authorId)).thenReturn(Optional.of(authorEntity));
        when(bookRepositoryJpa.save(any(BookEntity.class))).thenReturn(persisted);

        BookModel saved = repository.save(book);

        assertNotNull(saved);
        assertEquals("Computing Foundations", saved.getTitle());
    }

    @Test
    @DisplayName("Check that repository update modifies and saves existing book")
    void updateShouldPersistFields() {
        BookRepositoryImpl repository = new BookRepositoryImpl(bookRepositoryJpa, authorRepositoryJpa);
        UUID id = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        AuthorModel authorModel = new AuthorModel("Ada", "Lovelace", "ada@history.dev", "First programmer");
        authorModel.setId(authorId);
        BookModel book = new BookModel("Updated title", "Updated description", authorModel);

        AuthorEntity authorEntity = new AuthorEntity("Ada", "Lovelace", "ada@history.dev", "First programmer");
        BookEntity existing = new BookEntity("Old title", "Old description", authorEntity);
        when(bookRepositoryJpa.findById(id)).thenReturn(Optional.of(existing));
        when(authorRepositoryJpa.findById(authorId)).thenReturn(Optional.of(authorEntity));
        when(bookRepositoryJpa.save(existing)).thenReturn(existing);

        BookModel updated = repository.update(id, book);

        assertNotNull(updated);
        verify(bookRepositoryJpa).save(existing);
    }
}
