package com.example.moonshot.userselecteddestination.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserSelectedDestinationRequest {
    private UUID userId;
    private UUID destinationId;
}
