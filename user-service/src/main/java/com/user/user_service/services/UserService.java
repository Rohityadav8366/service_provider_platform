package com.user.user_service.services;


import com.user.user_service.DTOs.LoginRequest;
import com.user.user_service.DTOs.LoginResponse;
import com.user.user_service.DTOs.UserProfileResponse;
import com.user.user_service.DTOs.UserRegistrationRequest;

/**
 * User Service Interface
 * Defines business logic operations for User management
 */
public interface UserService {

    /**
     * Register a new user (Customer or Provider)
     */
    UserProfileResponse registerUser(UserRegistrationRequest request);

    /**
     * Authenticate user and generate JWT token
     */
    LoginResponse login(LoginRequest request);

    /**
     * Get user profile by ID
     */
    UserProfileResponse getUserProfile(Long userId);

    /**
     * Verify user email with token
     */
    void verifyEmail(String token);

    /**
     * Check if email exists
     */
    Boolean emailExists(String email);
}