package com.example.moonshot.country.dto;

import com.example.moonshot.country.Country;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CountryResponse {
    private UUID id;
    private String name;
    private boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CountryResponse from(Country country) {
        return CountryResponse.builder()
                .id(country.getId())
                .name(country.getName())
                .available(country.isAvailable())
                .createdAt(country.getCreatedAt())
                .updatedAt(country.getUpdatedAt())
                .build();
    }
}
