package com.example.moonshot.itinerary.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ItineraryRequest {
    private String title;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private Float rating;
    private UUID destinationId;
    private String imageUrl;
}
