package com.example.moonshot.itinerarystep;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItineraryStepRepository extends JpaRepository<ItineraryStep, UUID> {
    List<ItineraryStep> findByItinerary_IdOrderByStepIndexAsc(UUID itineraryId);
}
