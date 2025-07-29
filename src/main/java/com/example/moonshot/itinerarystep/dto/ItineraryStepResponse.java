package com.example.moonshot.itinerarystep.dto;

import com.example.moonshot.itinerarystep.ItineraryStep;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ItineraryStepResponse {
    private UUID id;
    private UUID itineraryId;
    private int stepIndex;
    private String title;
    private String description;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ItineraryStepResponse from(ItineraryStep step) {
        return ItineraryStepResponse.builder()
                .id(step.getId())
                .itineraryId(step.getItinerary().getId())
                .stepIndex(step.getStepIndex())
                .title(step.getTitle())
                .description(step.getDescription())
                .latitude(step.getLatitude())
                .longitude(step.getLongitude())
                .createdAt(step.getCreatedAt())
                .updatedAt(step.getUpdatedAt())
                .build();
    }
}
