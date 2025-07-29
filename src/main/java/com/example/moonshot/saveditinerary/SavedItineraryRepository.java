package com.example.moonshot.saveditinerary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import java.util.List;

@Repository
public interface SavedItineraryRepository extends JpaRepository<SavedItinerary, UUID> {
    List<SavedItinerary> findByUserId(UUID userId);
}

