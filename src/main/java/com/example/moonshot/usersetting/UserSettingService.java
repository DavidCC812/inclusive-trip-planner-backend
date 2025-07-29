package com.example.moonshot.usersetting;

import com.example.moonshot.exception.MoonshotException;
import com.example.moonshot.setting.Setting;
import com.example.moonshot.setting.SettingRepository;
import com.example.moonshot.user.User;
import com.example.moonshot.user.UserRepository;
import com.example.moonshot.usersetting.dto.UserSettingRequest;
import com.example.moonshot.usersetting.dto.UserSettingResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserSettingService {

    private final UserSettingRepository userSettingRepository;
    private final UserRepository userRepository;
    private final SettingRepository settingRepository;

    public UserSettingService(UserSettingRepository userSettingRepository,
                              UserRepository userRepository,
                              SettingRepository settingRepository) {
        this.userSettingRepository = userSettingRepository;
        this.userRepository = userRepository;
        this.settingRepository = settingRepository;
    }

    public List<UserSettingResponse> getAllUserSettings() {
        return userSettingRepository.findAll()
                .stream()
                .map(UserSettingResponse::from)
                .collect(Collectors.toList());
    }

    public UserSetting getUserSettingById(UUID id) {
        return userSettingRepository.findById(id)
                .orElseThrow(() -> new MoonshotException("UserSetting not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public UserSetting updateUserSetting(UUID id, UserSettingRequest request) {
        UserSetting existing = userSettingRepository.findById(id)
                .orElseThrow(() -> new MoonshotException("UserSetting not found", HttpStatus.NOT_FOUND));

        existing.setValue(request.isValue());
        existing.setUpdatedAt(LocalDateTime.now());

        return userSettingRepository.save(existing);
    }

    @Transactional
    public UserSetting createUserSetting(UserSettingRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new MoonshotException("User not found", HttpStatus.NOT_FOUND));

        Setting setting = settingRepository.findById(request.getSettingId())
                .orElseThrow(() -> new MoonshotException("Setting not found", HttpStatus.NOT_FOUND));

        UserSetting userSetting = UserSetting.builder()
                .user(user)
                .setting(setting)
                .value(request.isValue())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return userSettingRepository.save(userSetting);
    }

    public void deleteUserSetting(UUID id) {
        userSettingRepository.deleteById(id);
    }
}
