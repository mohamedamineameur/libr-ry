package com.example.app.dtos.bookDTO;

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

class BookDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Check that create book request requires mandatory fields")
    void createBookRequestShouldRequireMandatoryFields() {
        CreateBookRequest dto = new CreateBookRequest();
        Set<ConstraintViolation<CreateBookRequest>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Check that update book request accepts optional partial updates")
    void updateBookRequestShouldAllowOptionalFields() {
        UpdateBookRequest dto = new UpdateBookRequest();
        Set<ConstraintViolation<UpdateBookRequest>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}
