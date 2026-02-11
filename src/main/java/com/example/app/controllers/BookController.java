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
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dtos.bookDTO.BookResponse;
import com.example.app.dtos.bookDTO.CreateBookRequest;
import com.example.app.dtos.bookDTO.UpdateBookRequest;
import com.example.app.security.RequireActive;
import com.example.app.security.RequireAdmin;
import com.example.app.security.RequireAuthenticated;
import com.example.app.services.BookService;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @RequireAuthenticated
    @RequireActive
    @RequireAdmin
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody CreateBookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(request));
    }

    @PutMapping("/{id}")
    @RequireAuthenticated
    @RequireActive
    @RequireAdmin
    public ResponseEntity<BookResponse> updateBook(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateBookRequest request
    ) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @GetMapping
    @RequireAuthenticated
    @RequireActive
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    @RequireAuthenticated
    @RequireActive
    public ResponseEntity<BookResponse> getBookById(@PathVariable UUID id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @DeleteMapping("/{id}")
    @RequireAuthenticated
    @RequireActive
    @RequireAdmin
    public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
