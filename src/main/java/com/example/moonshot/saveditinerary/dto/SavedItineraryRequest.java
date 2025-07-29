package com.example.moonshot.saveditinerary.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SavedItineraryRequest {
    private UUID userId;
    private UUID itineraryId;
}
