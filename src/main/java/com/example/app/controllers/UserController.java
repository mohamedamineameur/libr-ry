package com.example.app.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dtos.userDTO.ChangePasswordRequest;
import com.example.app.dtos.userDTO.CreateUserRequest;
import com.example.app.dtos.userDTO.LoginRequest;
import com.example.app.dtos.userDTO.SetActiveRequest;
import com.example.app.dtos.userDTO.SetAdminRequest;
import com.example.app.dtos.userDTO.UpdateUserRequest;
import com.example.app.dtos.userDTO.UserResponse;
import com.example.app.services.UserService;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(request));
    }

    @PatchMapping("/password/{id}")
    public ResponseEntity<UserResponse> changePassword(
        @PathVariable UUID id,
        @Valid @RequestBody ChangePasswordRequest request
    ) {
        return ResponseEntity.ok(userService.changePassword(id, request));
    }

    @PatchMapping("/active")
    public ResponseEntity<UserResponse> setActive(@Valid @RequestBody SetActiveRequest request) {
        return ResponseEntity.ok(userService.setActive(request));
    }

    @PatchMapping("/admin")
    public ResponseEntity<UserResponse> setAdmin(@Valid @RequestBody SetAdminRequest request) {
        return ResponseEntity.ok(userService.setAdmin(request));
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request) {
        userService.login(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
   
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        return ResponseEntity.ok(userService.getMe());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
