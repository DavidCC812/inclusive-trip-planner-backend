package com.example.moonshot.accessibilityfeature;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccessibilityFeatureRepository extends JpaRepository<AccessibilityFeature, UUID> {
}
