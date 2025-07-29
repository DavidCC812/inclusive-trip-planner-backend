package com.example.moonshot.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequest {
    private String name;
    private String nickname;
    private String email;
    private String passwordHash;
    private String platform;
    private String phone;
}
