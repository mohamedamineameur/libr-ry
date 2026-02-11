package com.example.app.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.app.infrastructure.entities.UserEntity;
import com.example.app.models.UserModel;

class UserMapperTest {

    @Test
    @DisplayName("Check that mapper converts entity to domain with key fields")
    void toDomainShouldMapImportantFields() {
        UserEntity entity = mock(UserEntity.class);
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        when(entity.getId()).thenReturn(id);
        when(entity.getName()).thenReturn("John");
        when(entity.getEmail()).thenReturn("john@example.com");
        when(entity.getPassword()).thenReturn("hashed");
        when(entity.getCreatedAt()).thenReturn(now);
        when(entity.getUpdatedAt()).thenReturn(now);
        when(entity.getIsActive()).thenReturn(true);
        when(entity.getIsAdmin()).thenReturn(false);

        UserModel model = UserMapper.toDomain(entity);

        assertNotNull(model.getId());
        assertEquals(id, model.getId());
        assertEquals("john@example.com", model.getEmail());
    }

    @Test
    @DisplayName("Check that mapper converts domain to entity with key fields")
    void toEntityShouldMapImportantFields() {
        UserModel model = new UserModel("John", "john@example.com", "hashed");
        model.setId(UUID.randomUUID());
        model.setIsActive(true);
        model.setIsAdmin(true);

        UserEntity entity = UserMapper.toEntity(model);

        assertEquals("john@example.com", entity.getEmail());
        assertEquals(true, entity.getIsActive());
        assertEquals(true, entity.getIsAdmin());
    }
}
