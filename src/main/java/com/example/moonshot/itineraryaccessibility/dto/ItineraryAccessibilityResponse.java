package com.example.moonshot.itineraryaccessibility.dto;

import com.example.moonshot.itineraryaccessibility.ItineraryAccessibility;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ItineraryAccessibilityResponse {
    private UUID id;
    private UUID itineraryId;
    private UUID featureId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ItineraryAccessibilityResponse from(ItineraryAccessibility entity) {
        return ItineraryAccessibilityResponse.builder()
                .id(entity.getId())
                .itineraryId(entity.getItinerary().getId())
                .featureId(entity.getFeature().getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
