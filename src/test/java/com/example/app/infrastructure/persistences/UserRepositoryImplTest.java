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

import com.example.app.infrastructure.entities.UserEntity;
import com.example.app.models.UserModel;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class UserRepositoryImplTest {

    @Mock
    private UserRepositoryJpa userRepositoryJpa;

    @Test
    @DisplayName("Check that repository maps findByEmail result to domain model")
    void findByEmailShouldMapEntityToDomain() {
        UserRepositoryImpl repository = new UserRepositoryImpl(userRepositoryJpa);
        UserEntity entity = new UserEntity("John", "john@example.com", "hashed");
        when(userRepositoryJpa.findByEmail("john@example.com")).thenReturn(Optional.of(entity));

        UserModel model = repository.findByEmail("john@example.com");

        assertEquals("john@example.com", model.getEmail());
    }

    @Test
    @DisplayName("Check that setAdmin persists admin flag updates")
    void setAdminShouldPersistFlag() {
        UserRepositoryImpl repository = new UserRepositoryImpl(userRepositoryJpa);
        UUID id = UUID.randomUUID();
        UserEntity entity = new UserEntity("John", "john@example.com", "hashed");
        when(userRepositoryJpa.findById(id)).thenReturn(Optional.of(entity));
        when(userRepositoryJpa.save(entity)).thenReturn(entity);

        UserModel updated = repository.setAdmin(id, true);

        assertNotNull(updated);
        verify(userRepositoryJpa).save(entity);
    }
}
