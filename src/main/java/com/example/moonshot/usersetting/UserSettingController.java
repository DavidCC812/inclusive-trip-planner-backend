package com.example.moonshot.usersetting;

import com.example.moonshot.usersetting.dto.UserSettingRequest;
import com.example.moonshot.usersetting.dto.UserSettingResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-settings")
public class UserSettingController {

    private final UserSettingService userSettingService;

    public UserSettingController(UserSettingService userSettingService) {
        this.userSettingService = userSettingService;
    }

    @GetMapping
    public List<UserSettingResponse> getAllUserSettings() {
        return userSettingService.getAllUserSettings();
    }

    @GetMapping("/{id}")
    public UserSettingResponse getUserSettingById(@PathVariable UUID id) {
        UserSetting setting = userSettingService.getUserSettingById(id);
        return UserSettingResponse.from(setting);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserSettingResponse createUserSetting(@RequestBody UserSettingRequest request) {
        UserSetting created = userSettingService.createUserSetting(request);
        return UserSettingResponse.from(created);
    }

    @PutMapping("/{id}")
    public UserSettingResponse updateUserSetting(
            @PathVariable UUID id,
            @RequestBody UserSettingRequest request) {
        UserSetting updated = userSettingService.updateUserSetting(id, request);
        return UserSettingResponse.from(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserSetting(@PathVariable UUID id) {
        userSettingService.deleteUserSetting(id);
    }
}
