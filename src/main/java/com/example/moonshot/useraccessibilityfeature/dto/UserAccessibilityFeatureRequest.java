package com.example.moonshot.useraccessibilityfeature.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserAccessibilityFeatureRequest {
    private UUID userId;
    private UUID featureId;
}
