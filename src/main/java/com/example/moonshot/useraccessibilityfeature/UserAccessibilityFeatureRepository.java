package com.example.moonshot.useraccessibilityfeature;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserAccessibilityFeatureRepository extends JpaRepository<UserAccessibilityFeature, UUID> {
}
