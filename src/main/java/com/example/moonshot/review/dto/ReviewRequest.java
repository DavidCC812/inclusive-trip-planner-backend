package com.example.moonshot.review.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ReviewRequest {
    private UUID userId;
    private UUID itineraryId;
    private Float rating;
    private String comment;
}
