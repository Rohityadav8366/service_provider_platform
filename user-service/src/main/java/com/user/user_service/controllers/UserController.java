package com.user.user_service.controllers;

import com.user.user_service.DTOs.*;
import com.user.user_service.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 * Handles HTTP requests for user management
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Register a new user (Customer or Provider)
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserProfileResponse>> registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {

        log.info("Register request received for email: {}", request.getEmail());

        UserProfileResponse response = userService.registerUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully. Please verify your email.", response));
    }

    /**
     * Login user and get JWT token
     * POST /api/users/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        log.info("Login request received for email: {}", request.getEmail());

        LoginResponse response = userService.login(request);

        return ResponseEntity
                .ok(ApiResponse.success("Login successful", response));
    }

    /**
     * Get user profile by ID
     * GET /api/users/profile/{userId}
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(
            @PathVariable Long userId) {

        log.info("Get profile request for user ID: {}", userId);

        UserProfileResponse response = userService.getUserProfile(userId);

        return ResponseEntity
                .ok(ApiResponse.success("Profile retrieved successfully", response));
    }

    /**
     * Verify email with token
     * GET /api/users/verify-email?token=xxx
     */
    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<Object>> verifyEmail(
            @RequestParam String token) {

        log.info("Email verification request received");

        userService.verifyEmail(token);

        return ResponseEntity
                .ok(ApiResponse.success("Email verified successfully", null));
    }

    /**
     * Check if email exists
     * GET /api/users/check-email?email=xxx
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(
            @RequestParam String email) {

        log.info("Check email request for: {}", email);

        Boolean exists = userService.emailExists(email);

        return ResponseEntity
                .ok(ApiResponse.success("Email check completed", exists));
    }

    /**
     * Health check endpoint
     * GET /api/users/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User Service is running!");
    }
}
