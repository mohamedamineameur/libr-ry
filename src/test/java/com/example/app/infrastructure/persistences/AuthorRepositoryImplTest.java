package com.example.app.infrastructure.persistences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import com.example.app.models.AuthorModel;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class AuthorRepositoryImplTest {

    @Mock
    private AuthorRepositoryJpa authorRepositoryJpa;

    @Test
    @DisplayName("Check that repository maps findByEmail result to author domain model")
    void findByEmailShouldMapEntityToDomain() {
        AuthorRepositoryImpl repository = new AuthorRepositoryImpl(authorRepositoryJpa);
        AuthorEntity entity = new AuthorEntity("Ada", "Lovelace", "ada@history.dev", "First programmer");
        when(authorRepositoryJpa.findByEmail("ada@history.dev")).thenReturn(Optional.of(entity));

        AuthorModel model = repository.findByEmail("ada@history.dev");

        assertEquals("Ada", model.getFirstName());
        assertEquals("Lovelace", model.getLastName());
    }

    @Test
    @DisplayName("Check that update persists author field modifications")
    void updateShouldPersistFields() {
        AuthorRepositoryImpl repository = new AuthorRepositoryImpl(authorRepositoryJpa);
        UUID id = UUID.randomUUID();
        AuthorEntity entity = new AuthorEntity("Ada", "Lovelace", "ada@history.dev", "First programmer");
        when(authorRepositoryJpa.findById(id)).thenReturn(Optional.of(entity));
        when(authorRepositoryJpa.save(entity)).thenReturn(entity);

        AuthorModel updated = repository.update(id, "Grace", "Hopper", "grace@history.dev", "COBOL pioneer");

        assertNotNull(updated);
        verify(authorRepositoryJpa).save(entity);
    }
}
