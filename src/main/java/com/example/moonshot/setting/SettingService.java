package com.example.moonshot.setting;

import com.example.moonshot.exception.MoonshotException;
import com.example.moonshot.setting.dto.SettingRequest;
import com.example.moonshot.setting.dto.SettingResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SettingService {

    private final SettingRepository settingRepository;

    public SettingService(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    public List<SettingResponse> getAllSettings() {
        return settingRepository.findAll()
                .stream()
                .map(SettingResponse::from)
                .collect(Collectors.toList());
    }

    public Setting getSettingById(UUID id) {
        return settingRepository.findById(id)
                .orElseThrow(() -> new MoonshotException("Setting not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Setting createSetting(SettingRequest dto) {
        Setting setting = Setting.builder()
                .settingKey(dto.getSettingKey())
                .label(dto.getLabel())
                .description(dto.getDescription())
                .defaultValue(dto.isDefaultValue())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return settingRepository.save(setting);
    }

    public void deleteSetting(UUID id) {
        settingRepository.deleteById(id);
    }
}
