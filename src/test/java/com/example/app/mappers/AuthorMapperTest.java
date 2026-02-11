package com.example.app.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.app.infrastructure.entities.AuthorEntity;
import com.example.app.models.AuthorModel;

class AuthorMapperTest {

    @Test
    @DisplayName("Check that mapper converts author entity to domain with key fields")
    void toDomainShouldMapImportantFields() {
        AuthorEntity entity = mock(AuthorEntity.class);
        UUID id = UUID.randomUUID();
        when(entity.getId()).thenReturn(id);
        when(entity.getFirstName()).thenReturn("Ada");
        when(entity.getLastName()).thenReturn("Lovelace");
        when(entity.getEmail()).thenReturn("ada@history.dev");
        when(entity.getBiography()).thenReturn("First programmer");

        AuthorModel model = AuthorMapper.toDomain(entity);

        assertNotNull(model.getId());
        assertEquals(id, model.getId());
        assertEquals("Ada", model.getFirstName());
    }

    @Test
    @DisplayName("Check that mapper converts author domain to entity with key fields")
    void toEntityShouldMapImportantFields() {
        AuthorModel model = new AuthorModel("Ada", "Lovelace", "ada@history.dev", "First programmer");

        AuthorEntity entity = AuthorMapper.toEntity(model);

        assertEquals("Ada", entity.getFirstName());
        assertEquals("Lovelace", entity.getLastName());
        assertEquals("ada@history.dev", entity.getEmail());
    }
}
