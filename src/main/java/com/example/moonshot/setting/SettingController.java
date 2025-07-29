package com.example.moonshot.setting;

import com.example.moonshot.setting.dto.SettingRequest;
import com.example.moonshot.setting.dto.SettingResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/settings")
public class SettingController {

    private final SettingService settingService;

    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping
    public List<SettingResponse> getAllSettings() {
        return settingService.getAllSettings();
    }

    @GetMapping("/{id}")
    public SettingResponse getSettingById(@PathVariable UUID id) {
        Setting setting = settingService.getSettingById(id);
        return SettingResponse.from(setting);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SettingResponse createSetting(@RequestBody SettingRequest request) {
        Setting setting = settingService.createSetting(request);
        return SettingResponse.from(setting);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSetting(@PathVariable UUID id) {
        settingService.deleteSetting(id);
    }
}
