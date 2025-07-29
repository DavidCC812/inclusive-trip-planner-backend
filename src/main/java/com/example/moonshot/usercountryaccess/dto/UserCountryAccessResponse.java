package com.example.moonshot.usercountryaccess.dto;

import com.example.moonshot.usercountryaccess.UserCountryAccess;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserCountryAccessResponse {
    private UUID id;
    private UUID userId;
    private UUID countryId;
    private String countryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserCountryAccessResponse from(UserCountryAccess access) {
        return UserCountryAccessResponse.builder()
                .id(access.getId())
                .userId(access.getUser().getId())
                .countryId(access.getCountry().getId())
                .countryName(access.getCountry().getName())
                .createdAt(access.getCreatedAt())
                .updatedAt(access.getUpdatedAt())
                .build();
    }
}
