package com.example.moonshot.destination.dto;

import com.example.moonshot.destination.Destination;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DestinationResponse {
    private UUID id;
    private String name;
    private String type;
    private boolean available;
    private UUID countryId;
    private String countryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DestinationResponse from(Destination destination) {
        return DestinationResponse.builder()
                .id(destination.getId())
                .name(destination.getName())
                .type(destination.getType())
                .available(destination.isAvailable())
                .countryId(destination.getCountry().getId())
                .countryName(destination.getCountry().getName())
                .createdAt(destination.getCreatedAt())
                .updatedAt(destination.getUpdatedAt())
                .build();
    }
}
