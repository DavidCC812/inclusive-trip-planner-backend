package com.example.moonshot.itineraryaccessibility;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ItineraryAccessibilityRepository extends JpaRepository<ItineraryAccessibility, UUID> {
}
