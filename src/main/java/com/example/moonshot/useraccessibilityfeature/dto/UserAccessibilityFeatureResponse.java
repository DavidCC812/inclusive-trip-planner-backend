package com.example.moonshot.useraccessibilityfeature.dto;

import com.example.moonshot.useraccessibilityfeature.UserAccessibilityFeature;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserAccessibilityFeatureResponse {
    private UUID id;
    private UUID userId;
    private UUID featureId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserAccessibilityFeatureResponse from(UserAccessibilityFeature link) {
        return UserAccessibilityFeatureResponse.builder()
                .id(link.getId())
                .userId(link.getUser().getId())
                .featureId(link.getFeature().getId())
                .createdAt(link.getCreatedAt())
                .updatedAt(link.getUpdatedAt())
                .build();
    }
}
