package com.example.app.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dtos.authorDTO.AuthorResponse;
import com.example.app.dtos.authorDTO.CreateAuthorRequest;
import com.example.app.dtos.authorDTO.UpdateAuthorRequest;
import com.example.app.security.RequireActive;
import com.example.app.security.RequireAdmin;
import com.example.app.security.RequireAuthenticated;
import com.example.app.services.AuthorService;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    @RequireAuthenticated
    @RequireActive
    @RequireAdmin
    public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody CreateAuthorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.createAuthor(request));
    }

    @PutMapping("/{id}")
    @RequireAuthenticated
    @RequireActive
    @RequireAdmin
    public ResponseEntity<AuthorResponse> updateAuthor(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateAuthorRequest request
    ) {
        return ResponseEntity.ok(authorService.updateAuthor(id, request));
    }

    @GetMapping
    @RequireAuthenticated
    @RequireActive
    public ResponseEntity<List<AuthorResponse>> getAllAuthors(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(authorService.getAllAuthors(page, size));
    }

    @GetMapping("/{id}")
    @RequireAuthenticated
    @RequireActive
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable UUID id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @DeleteMapping("/{id}")
    @RequireAuthenticated
    @RequireActive
    @RequireAdmin
    public ResponseEntity<Void> deleteAuthor(@PathVariable UUID id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}
