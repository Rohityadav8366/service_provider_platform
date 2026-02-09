package com.user.user_service.DTOs;

import com.user.user_service.entity.UserRole;
import com.user.user_service.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for User Profile Response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String email;
    private UserRole role;
    private UserStatus status;
    private Boolean emailVerified;
    private String fullName;
    private String phone;
    private String city;
    private String state;
    private String profileImageUrl;

    // Provider specific fields
    private String businessName;
    private String specialization;
    private Integer experienceYears;
    private Boolean isVerified;

    private LocalDateTime createdAt;
}
