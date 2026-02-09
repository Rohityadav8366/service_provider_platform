package com.user.user_service.services;

import com.user.user_service.DTOs.LoginRequest;
import com.user.user_service.DTOs.LoginResponse;
import com.user.user_service.DTOs.UserProfileResponse;
import com.user.user_service.DTOs.UserRegistrationRequest;
import com.user.user_service.entity.*;
import com.user.user_service.exception.DuplicateUserException;
import com.user.user_service.exception.InvalidCredentialsException;
import com.user.user_service.exception.UserNotFoundException;
import com.user.user_service.repositories.CustomerProfileRepository;
import com.user.user_service.repositories.ProviderProfileRepository;
import com.user.user_service.repositories.UserRepository;
import com.user.user_service.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * User Service Implementation
 * Contains business logic for user management
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public UserProfileResponse registerUser(UserRegistrationRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException("Email already registered: " + request.getEmail());
        }

        // Create User entity
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);
        user.setVerificationToken(UUID.randomUUID().toString());

        user = userRepository.save(user);
        log.info("User created with ID: {}", user.getId());

        // Create profile based on role
        if (request.getRole() == UserRole.CUSTOMER) {
            createCustomerProfile(user, request);
        } else if (request.getRole() == UserRole.PROVIDER) {
            createProviderProfile(user, request);
        }

        // TODO: Send verification email asynchronously via Kafka

        return mapToUserProfileResponse(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Check if account is active
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidCredentialsException("Account is not active");
        }

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user);

        log.info("User logged in successfully: {}", user.getEmail());

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .expiresIn(jwtTokenProvider.getExpirationInSeconds())
                .build();
    }

    @Override
    public UserProfileResponse getUserProfile(Long userId) {
        log.info("Fetching user profile for ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        return mapToUserProfileResponse(user);
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        log.info("Verifying email with token: {}", token);

        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new UserNotFoundException("Invalid verification token"));

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        log.info("Email verified for user: {}", user.getEmail());
    }

    @Override
    public Boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // Helper Methods

    private void createCustomerProfile(User user, UserRegistrationRequest request) {
        CustomerProfile profile = new CustomerProfile();
        profile.setUser(user);
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        customerProfileRepository.save(profile);
        log.info("Customer profile created for user ID: {}", user.getId());
    }

    private void createProviderProfile(User user, UserRegistrationRequest request) {
        ProviderProfile profile = new ProviderProfile();
        profile.setUser(user);
        profile.setBusinessName(request.getBusinessName() != null ?
                request.getBusinessName() : request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setSpecialization(request.getSpecialization());
        profile.setExperienceYears(request.getExperienceYears() != null ?
                request.getExperienceYears() : 0);
        profile.setIsVerified(false);
        profile.setIsAvailable(true);
        providerProfileRepository.save(profile);
        log.info("Provider profile created for user ID: {}", user.getId());
    }

    private UserProfileResponse mapToUserProfileResponse(User user) {
        UserProfileResponse.UserProfileResponseBuilder builder = UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .emailVerified(user.getEmailVerified())
                .createdAt(user.getCreatedAt());

        // Add customer profile details if customer
        if (user.getRole() == UserRole.CUSTOMER && user.getCustomerProfile() != null) {
            CustomerProfile profile = user.getCustomerProfile();
            builder.fullName(profile.getFullName())
                    .phone(profile.getPhone())
                    .city(profile.getCity())
                    .state(profile.getState())
                    .profileImageUrl(profile.getProfileImageUrl());
        }

        // Add provider profile details if provider
        if (user.getRole() == UserRole.PROVIDER && user.getProviderProfile() != null) {
            ProviderProfile profile = user.getProviderProfile();
            builder.fullName(profile.getBusinessName())
                    .phone(profile.getPhone())
                    .businessName(profile.getBusinessName())
                    .specialization(profile.getSpecialization())
                    .experienceYears(profile.getExperienceYears())
                    .isVerified(profile.getIsVerified())
                    .city(profile.getCity())
                    .state(profile.getState())
                    .profileImageUrl(profile.getProfileImageUrl());
        }

        return builder.build();
    }
}