package com.example.app.dtos.userDTO;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.UUID;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Check that update user request requires user id")
    void updateUserRequestShouldRequireId() {
        UpdateUserRequest dto = new UpdateUserRequest();
        dto.setName("John");
        dto.setEmail("john@example.com");

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Check that set active request requires user id and active state")
    void setActiveRequestShouldRequireUserIdAndState() {
        SetActiveRequest dto = new SetActiveRequest();

        Set<ConstraintViolation<SetActiveRequest>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Check that set admin request is valid when required fields are present")
    void setAdminRequestShouldBeValidWhenValuesArePresent() {
        SetAdminRequest dto = new SetAdminRequest();
        dto.setUserId(UUID.randomUUID());
        dto.setIsAdmin(Boolean.TRUE);

        Set<ConstraintViolation<SetAdminRequest>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}
