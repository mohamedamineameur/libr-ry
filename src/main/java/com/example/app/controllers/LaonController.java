package com.example.app.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dtos.laonDTO.CreateLaonRequest;
import com.example.app.dtos.laonDTO.LaonResponse;
import com.example.app.dtos.laonDTO.MarkLaonReturnedRequest;
import com.example.app.security.RequireActive;
import com.example.app.security.RequireAdmin;
import com.example.app.security.RequireAuthenticated;
import com.example.app.services.LaonService;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/laons")
public class LaonController {

    private final LaonService laonService;

    public LaonController(LaonService laonService) {
        this.laonService = laonService;
    }

    @PostMapping
    @RequireAuthenticated
    @RequireActive
    public ResponseEntity<LaonResponse> createLaon(@Valid @RequestBody CreateLaonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(laonService.createLaon(request));
    }

    @GetMapping("/{id}")
    @RequireAuthenticated
    @RequireActive
    public ResponseEntity<LaonResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(laonService.getById(id));
    }

    @GetMapping("/me")
    @RequireAuthenticated
    @RequireActive
    public ResponseEntity<List<LaonResponse>> getMine() {
        return ResponseEntity.ok(laonService.getMine());
    }

    @GetMapping
    @RequireAuthenticated
    @RequireActive
    @RequireAdmin
    public ResponseEntity<List<LaonResponse>> getAll() {
        return ResponseEntity.ok(laonService.getAll());
    }

    @PatchMapping("/{id}/return")
    @RequireAuthenticated
    @RequireActive
    public ResponseEntity<LaonResponse> markReturned(
        @PathVariable UUID id,
        @Valid @RequestBody MarkLaonReturnedRequest request
    ) {
        return ResponseEntity.ok(laonService.markReturned(id, request));
    }
}
