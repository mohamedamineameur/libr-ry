package com.example.app.dtos.authorDTO;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class AuthorDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Check that create author request requires mandatory fields")
    void createAuthorRequestShouldRequireMandatoryFields() {
        CreateAuthorRequest dto = new CreateAuthorRequest();
        Set<ConstraintViolation<CreateAuthorRequest>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Check that update author request accepts optional partial updates")
    void updateAuthorRequestShouldAllowOptionalFields() {
        UpdateAuthorRequest dto = new UpdateAuthorRequest();
        Set<ConstraintViolation<UpdateAuthorRequest>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}
