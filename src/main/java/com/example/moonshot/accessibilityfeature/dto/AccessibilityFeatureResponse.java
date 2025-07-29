package com.example.moonshot.accessibilityfeature.dto;

import com.example.moonshot.accessibilityfeature.AccessibilityFeature;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AccessibilityFeatureResponse {
    private UUID id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AccessibilityFeatureResponse from(AccessibilityFeature feature) {
        return AccessibilityFeatureResponse.builder()
                .id(feature.getId())
                .name(feature.getName())
                .createdAt(feature.getCreatedAt())
                .updatedAt(feature.getUpdatedAt())
                .build();
    }
}
