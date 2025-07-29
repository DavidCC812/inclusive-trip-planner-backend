package com.example.moonshot.setting.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SettingRequest {
    private String settingKey;
    private String label;
    private String description;
    private boolean defaultValue;
}
