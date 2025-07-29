package com.example.moonshot.saveditinerary.dto;

import com.example.moonshot.saveditinerary.SavedItinerary;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SavedItineraryResponse {
    private UUID id;
    private UUID userId;
    private UUID itineraryId;
    private LocalDateTime savedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SavedItineraryResponse from(SavedItinerary entity) {
        return SavedItineraryResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .itineraryId(entity.getItinerary().getId())
                .savedAt(entity.getSavedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
