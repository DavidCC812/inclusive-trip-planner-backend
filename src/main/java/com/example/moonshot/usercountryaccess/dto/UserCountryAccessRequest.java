package com.example.moonshot.usercountryaccess.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserCountryAccessRequest {
    private UUID userId;
    private UUID countryId;
}
