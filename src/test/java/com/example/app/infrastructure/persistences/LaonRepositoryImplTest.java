package com.example.app.infrastructure.persistences;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
import com.example.app.infrastructure.entities.LaonEntity;
import com.example.app.infrastructure.entities.UserEntity;
import com.example.app.models.AuthorModel;
import com.example.app.models.BookModel;
import com.example.app.models.LaonModel;
import com.example.app.models.UserModel;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class LaonRepositoryImplTest {

    @Mock
    private LaonRepositoryJpa laonRepositoryJpa;

    @Mock
    private UserRepositoryJpa userRepositoryJpa;

    @Mock
    private BookRepositoryJpa bookRepositoryJpa;

    @Test
    @DisplayName("Check that repository saves loan with linked user and book")
    void saveShouldPersistLaon() {
        LaonRepositoryImpl repository = new LaonRepositoryImpl(laonRepositoryJpa, userRepositoryJpa, bookRepositoryJpa);

        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        UserModel user = new UserModel("A", "a@test.dev", "pw");
        user.setId(userId);
        AuthorModel author = new AuthorModel("Ada", "Lovelace", "ada@test.dev", "bio");
        author.setId(UUID.randomUUID());
        BookModel book = new BookModel("Book", "Desc", author);
        book.setId(bookId);
        LaonModel loan = new LaonModel(user, book);

        UserEntity userEntity = new UserEntity("A", "a@test.dev", "pw");
        BookEntity bookEntity = new BookEntity("Book", "Desc", new AuthorEntity("Ada", "Lovelace", "ada@test.dev", "bio"));
        LaonEntity persisted = new LaonEntity(userEntity, bookEntity);

        when(userRepositoryJpa.findById(userId)).thenReturn(Optional.of(userEntity));
        when(bookRepositoryJpa.findById(bookId)).thenReturn(Optional.of(bookEntity));
        when(laonRepositoryJpa.save(any(LaonEntity.class))).thenReturn(persisted);

        LaonModel saved = repository.save(loan);

        assertNotNull(saved);
    }
}
