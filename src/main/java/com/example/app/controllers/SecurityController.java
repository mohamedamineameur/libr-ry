package com.example.app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dtos.securityDTO.SecurityResponse;
import com.example.app.dtos.securityDTO.SetTwoFactorRequest;
import com.example.app.security.RequireAuthenticated;
import com.example.app.services.SecurityService;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/security")
public class SecurityController {

    private final SecurityService securityService;

    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping("/me")
    @RequireAuthenticated
    public ResponseEntity<SecurityResponse> me() {
        return ResponseEntity.ok(new SecurityResponse(securityService.me()));
    }

    @PatchMapping("/2fa")
    @RequireAuthenticated
    public ResponseEntity<SecurityResponse> setTwoFactor(@Valid @RequestBody SetTwoFactorRequest request) {
        return ResponseEntity.ok(new SecurityResponse(securityService.setTwoFactorEnabled(request.getEnabled())));
    }
}
