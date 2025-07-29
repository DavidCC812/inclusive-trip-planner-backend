package com.example.moonshot.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class LoginResponse {
    private UUID userId;
    private String name;
    private String email;
    private String phone;
    private String token;
}
