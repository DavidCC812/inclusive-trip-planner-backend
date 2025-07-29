package com.example.moonshot.itineraryaccessibility.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ItineraryAccessibilityRequest {
    private UUID itineraryId;
    private UUID featureId;
}
