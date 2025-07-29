package com.example.moonshot.setting.dto;

import com.example.moonshot.setting.Setting;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SettingResponse {
    private UUID id;
    private String settingKey;
    private String label;
    private String description;
    private boolean defaultValue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SettingResponse from(Setting setting) {
        return SettingResponse.builder()
                .id(setting.getId())
                .settingKey(setting.getSettingKey())
                .label(setting.getLabel())
                .description(setting.getDescription())
                .defaultValue(setting.isDefaultValue())
                .createdAt(setting.getCreatedAt())
                .updatedAt(setting.getUpdatedAt())
                .build();
    }
}
