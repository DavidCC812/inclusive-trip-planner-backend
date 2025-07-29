package com.example.moonshot.itinerary.dto;

import com.example.moonshot.itinerary.Itinerary;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ItineraryResponse {
    private UUID id;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private Float rating;
    private UUID destinationId;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ItineraryResponse from(Itinerary itinerary) {
        return ItineraryResponse.builder()
                .id(itinerary.getId())
                .title(itinerary.getTitle())
                .description(itinerary.getDescription())
                .price(itinerary.getPrice())
                .duration(itinerary.getDuration())
                .rating(itinerary.getRating())
                .destinationId(itinerary.getDestination().getId())
                .imageUrl(itinerary.getImageUrl())
                .createdAt(itinerary.getCreatedAt())
                .updatedAt(itinerary.getUpdatedAt())
                .build();
    }
}
