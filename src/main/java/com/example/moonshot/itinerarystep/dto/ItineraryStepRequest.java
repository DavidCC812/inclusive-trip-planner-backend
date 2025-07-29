package com.example.moonshot.itinerarystep.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ItineraryStepRequest {
    private UUID itineraryId;
    private int stepIndex;
    private String title;
    private String description;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
