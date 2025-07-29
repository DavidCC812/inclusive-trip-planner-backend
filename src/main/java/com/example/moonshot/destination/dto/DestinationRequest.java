package com.example.moonshot.destination.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DestinationRequest {
    private String name;
    private String type;
    private boolean available;
    private UUID countryId;
}
