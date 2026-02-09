package com.user.user_service.repositories;


import com.user.user_service.entity.ProviderProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Provider Profile Repository
 */
@Repository
public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, Long> {

    Optional<ProviderProfile> findByUserId(Long userId);

    Boolean existsByUserId(Long userId);

    List<ProviderProfile> findBySpecialization(String specialization);

    List<ProviderProfile> findByIsVerified(Boolean isVerified);

    List<ProviderProfile> findByIsAvailable(Boolean isAvailable);

    @Query("SELECT p FROM ProviderProfile p WHERE p.specialization = :specialization AND p.city = :city AND p.isVerified = true AND p.isAvailable = true")
    List<ProviderProfile> findAvailableProvidersBySpecializationAndCity(
            @Param("specialization") String specialization,
            @Param("city") String city
    );
}