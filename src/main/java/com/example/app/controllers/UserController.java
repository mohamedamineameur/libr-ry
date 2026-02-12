package com.example.app.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
import com.example.app.dtos.securityDTO.VerifyEmailRequest;
import com.example.app.dtos.securityDTO.VerifyEmailResponse;
import com.example.app.dtos.securityDTO.VerifyOtpRequest;
import com.example.app.security.RequireActive;
import com.example.app.security.RequireAdmin;
import com.example.app.security.RequireAuthenticated;
import com.example.app.services.SecurityService.VerifyEmailResult;
import com.example.app.services.SessionService;
import com.example.app.services.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final SessionService sessionService;

    public UserController(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @PutMapping
    @RequireAuthenticated
    @RequireActive
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(request));
    }

    @PatchMapping("/password/{id}")
    @RequireAuthenticated
    @RequireActive
    public ResponseEntity<UserResponse> changePassword(
        @PathVariable UUID id,
        @Valid @RequestBody ChangePasswordRequest request
    ) {
        return ResponseEntity.ok(userService.changePassword(id, request));
    }

    @PatchMapping("/active")
    @RequireAuthenticated
    @RequireAdmin
    @RequireActive
    public ResponseEntity<UserResponse> setActive(@Valid @RequestBody SetActiveRequest request) {
        return ResponseEntity.ok(userService.setActive(request));
    }

    @PatchMapping("/admin")
    @RequireAuthenticated
    @RequireAdmin
    @RequireActive
    public ResponseEntity<UserResponse> setAdmin(@Valid @RequestBody SetAdminRequest request) {
        return ResponseEntity.ok(userService.setAdmin(request));
    }

    @PostMapping("/login")
    @SuppressWarnings("null")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String loginResult = userService.login(
            request,
            resolveClientIp(httpRequest),
            resolveUserAgent(httpRequest)
        );

        if (loginResult.startsWith("2FA_REQUIRED:")) {
            String challengeToken = loginResult.substring("2FA_REQUIRED:".length());
            ResponseCookie challengeCookie = ResponseCookie.from("login_challenge", challengeToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(60 * 5)
                .build();
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                .header(HttpHeaders.SET_COOKIE, challengeCookie.toString())
                .body(java.util.Map.of("code", "OTP_REQUIRED", "message", "OTP sent to your email"));
        }

        ResponseCookie idCookie = ResponseCookie.from("token", loginResult)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .sameSite("Lax")
            .maxAge(60 * 60 * 24)
            .build();

        return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, idCookie.toString())
            .build();
    }

    @PostMapping("/login/otp")
    @SuppressWarnings("null")
    public ResponseEntity<Void> loginWithOtp(@Valid @RequestBody VerifyOtpRequest request, HttpServletRequest httpRequest) {
        String challengeToken = readCookieValue(httpRequest, "login_challenge");
        String token = userService.verifyOtpAndLogin(
            challengeToken,
            request.getOtp(),
            resolveClientIp(httpRequest),
            resolveUserAgent(httpRequest)
        );

        ResponseCookie authCookie = ResponseCookie.from("token", token)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .sameSite("Lax")
            .maxAge(60 * 60 * 24)
            .build();

        ResponseCookie clearChallenge = ResponseCookie.from("login_challenge", "")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .sameSite("Lax")
            .maxAge(0)
            .build();

        return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, authCookie.toString())
            .header(HttpHeaders.SET_COOKIE, clearChallenge.toString())
            .build();
    }

    @PutMapping("/verify-email/{id}")
    public ResponseEntity<VerifyEmailResponse> verifyEmail(@PathVariable UUID id, @Valid @RequestBody VerifyEmailRequest request) {
        VerifyEmailResult result = userService.verifyEmail(id, request.getToken());
        if (result == VerifyEmailResult.ALREADY_VERIFIED) {
            return ResponseEntity.ok(new VerifyEmailResponse("already_verified", "Email already verified."));
        }
        return ResponseEntity.ok(new VerifyEmailResponse("verified", "Email verified successfully."));
    }

    @PostMapping("/logout")
    @RequireAuthenticated
    public ResponseEntity<Void> logout() {
        sessionService.logoutCurrentSession();
        ResponseCookie clearTokenCookie = ResponseCookie.from("token", "")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .sameSite("Lax")
            .maxAge(0)
            .build();

        return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, clearTokenCookie.toString())
            .build();
    }

    @GetMapping
    @RequireAuthenticated
    @RequireAdmin
    @RequireActive
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
   
    @GetMapping("/me")
    @RequireAuthenticated
    @RequireActive
    public ResponseEntity<UserResponse> getMe() {
        return ResponseEntity.ok(userService.getMe());
    }

    @DeleteMapping("/{id}")
    @RequireAuthenticated
    @RequireActive
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String resolveUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent == null ? "" : userAgent;
    }

    private String readCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new com.example.app.exceptions.BusinessException(
                HttpStatus.UNAUTHORIZED,
                "MISSING_LOGIN_CHALLENGE",
                "Missing login challenge cookie"
            );
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                return cookie.getValue();
            }
        }
        throw new com.example.app.exceptions.BusinessException(
            HttpStatus.UNAUTHORIZED,
            "MISSING_LOGIN_CHALLENGE",
            "Missing login challenge cookie"
        );
    }
}
