package com.example.moonshot.userselecteddestination.dto;

import com.example.moonshot.userselecteddestination.UserSelectedDestination;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserSelectedDestinationResponse {
    private UUID id;
    private UUID userId;
    private UUID destinationId;
    private String destinationName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserSelectedDestinationResponse from(UserSelectedDestination entity) {
        return UserSelectedDestinationResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .destinationId(entity.getDestination().getId())
                .destinationName(entity.getDestination().getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
