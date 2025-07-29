package com.example.moonshot.usersetting.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserSettingRequest {
    private UUID userId;
    private UUID settingId;
    private boolean value;
}
