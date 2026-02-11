package com.example.app.exceptions;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.warn("Business error on {} {} [{}]: {}", request.getMethod(), request.getRequestURI(), ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus().value()).body(
            buildError(ex.getStatus(), ex.getCode(), ex.getMessage(), request.getRequestURI(), null)
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        log.warn("ResponseStatusException on {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getReason(), ex);
        return ResponseEntity.status(status).body(
            buildError(status, "RESPONSE_STATUS_EXCEPTION", ex.getReason(), request.getRequestURI(), null)
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> details = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.warn("Validation error on {} {}: {}", request.getMethod(), request.getRequestURI(), details);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            buildError(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", request.getRequestURI(), details)
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> details = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(v -> details.put(v.getPropertyPath().toString(), v.getMessage()));
        log.warn("Constraint violation on {} {}: {}", request.getMethod(), request.getRequestURI(), details);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            buildError(HttpStatus.BAD_REQUEST, "CONSTRAINT_VIOLATION", "Constraint violation", request.getRequestURI(), details)
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        Throwable cause = ex.getMostSpecificCause();
        if (cause instanceof UnrecognizedPropertyException unrecognizedPropertyException) {
            String field = unrecognizedPropertyException.getPropertyName();
            Map<String, String> details = new LinkedHashMap<>();
            details.put(field, "Unknown field");
            String message = "Unknown field: " + field;

            log.warn("Unknown JSON field on {} {}: {}", request.getMethod(), request.getRequestURI(), field);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                buildError(HttpStatus.BAD_REQUEST, "UNKNOWN_FIELD", message, request.getRequestURI(), details)
            );
        }

        log.warn("Malformed JSON on {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            buildError(HttpStatus.BAD_REQUEST, "MALFORMED_JSON", "Malformed request body", request.getRequestURI(), null)
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            buildError(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected server error", request.getRequestURI(), null)
        );
    }

    private ApiErrorResponse buildError(
        HttpStatus status,
        String code,
        String message,
        String path,
        Map<String, String> details
    ) {
        return new ApiErrorResponse(
            Instant.now(),
            status.value(),
            status.getReasonPhrase(),
            code,
            message,
            path,
            details
        );
    }
}
