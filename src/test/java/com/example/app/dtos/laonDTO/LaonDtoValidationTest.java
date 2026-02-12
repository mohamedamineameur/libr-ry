package com.example.app.dtos.laonDTO;

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

class LaonDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Check that create loan request requires user and book ids")
    void createLaonRequestShouldRequireMandatoryFields() {
        CreateLaonRequest dto = new CreateLaonRequest();
        Set<ConstraintViolation<CreateLaonRequest>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Check that mark returned request requires returned flag")
    void markReturnedRequestShouldRequireReturnedFlag() {
        MarkLaonReturnedRequest dto = new MarkLaonReturnedRequest();
        Set<ConstraintViolation<MarkLaonReturnedRequest>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());

        dto.setIsReturned(true);
        violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}
