package com.example.moonshot.usercountryaccess;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCountryAccessRepository extends JpaRepository<UserCountryAccess, UUID> {
    List<UserCountryAccess> findAllByUserId(UUID userId);
    Optional<UserCountryAccess> findByUserIdAndCountryId(UUID userId, UUID countryId);
}
