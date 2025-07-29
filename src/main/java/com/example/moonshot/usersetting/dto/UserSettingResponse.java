package com.example.moonshot.usersetting.dto;

import com.example.moonshot.usersetting.UserSetting;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserSettingResponse {
    private UUID id;
    private UUID userId;
    private UUID settingId;
    private boolean value;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserSettingResponse from(UserSetting setting) {
        return UserSettingResponse.builder()
                .id(setting.getId())
                .userId(setting.getUser().getId())
                .settingId(setting.getSetting().getId())
                .value(setting.isValue())
                .createdAt(setting.getCreatedAt())
                .updatedAt(setting.getUpdatedAt())
                .build();
    }
}
