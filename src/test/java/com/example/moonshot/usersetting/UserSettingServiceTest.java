package com.example.moonshot.usersetting;

import com.example.moonshot.auth.dto.LoginRequest;
import com.example.moonshot.datacleaner.TestDataCleaner;
import com.example.moonshot.setting.Setting;
import com.example.moonshot.setting.SettingRepository;
import com.example.moonshot.user.User;
import com.example.moonshot.user.UserRepository;
import com.example.moonshot.usersetting.dto.UserSettingRequest;
import com.example.moonshot.usersetting.dto.UserSettingResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserSettingServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserSettingRepository userSettingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TestDataCleaner cleaner;

    private User user;
    private String token;

    @BeforeEach
    void setup() throws Exception {
        cleaner.cleanAll();

        user = userRepository.save(User.builder()
                .name("Test User")
                .email("test@example.com")
                .passwordHash(passwordEncoder.encode("secure123"))
                .platform("EMAIL")
                .phone("123456789")
                .build());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("test@example.com");
        loginRequest.setPassword("secure123");

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        token = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void shouldCreateUserSettingAndFetchItById() throws Exception {
        Setting setting = settingRepository.save(Setting.builder()
                .settingKey("notify_challenges")
                .label("Notify on Challenges")
                .description("Get notified when a challenge is completed")
                .defaultValue(true)
                .build());

        UserSettingRequest request = UserSettingRequest.builder()
                .userId(user.getId())
                .settingId(setting.getId())
                .value(false)
                .build();

        MvcResult result = mockMvc.perform(post("/api/user-settings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(user.getId().toString()))
                .andExpect(jsonPath("$.settingId").value(setting.getId().toString()))
                .andExpect(jsonPath("$.value").value(false))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        UserSettingResponse created = objectMapper.readValue(responseBody, UserSettingResponse.class);

        mockMvc.perform(get("/api/user-settings/" + created.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(user.getId().toString()))
                .andExpect(jsonPath("$.settingId").value(setting.getId().toString()))
                .andExpect(jsonPath("$.value").value(false));

        assertThat(created.getUserId()).isEqualTo(user.getId());
        assertThat(created.getSettingId()).isEqualTo(setting.getId());
    }

    @Test
    void shouldReturnAllUserSettings() throws Exception {
        Setting setting = settingRepository.save(Setting.builder()
                .settingKey("notify_deals")
                .label("Notify Deals")
                .description("Receive notifications for deals")
                .defaultValue(true)
                .build());

        userSettingRepository.save(UserSetting.builder()
                .user(user)
                .setting(setting)
                .value(true)
                .build());

        mockMvc.perform(get("/api/user-settings")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldDeleteUserSetting() throws Exception {
        Setting setting = settingRepository.save(Setting.builder()
                .settingKey("notify_tips")
                .label("Notify Tips")
                .description("Enable tip notifications")
                .defaultValue(false)
                .build());

        UUID id = userSettingRepository.save(UserSetting.builder()
                .user(user)
                .setting(setting)
                .value(true)
                .build()).getId();

        mockMvc.perform(delete("/api/user-settings/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/user-settings/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateUserSetting() throws Exception {
        // Create a setting and save a user setting (initially true)
        Setting setting = settingRepository.save(Setting.builder()
                .settingKey("receive_updates")
                .label("Receive Updates")
                .description("Enable updates")
                .defaultValue(true)
                .build());

        UUID settingId = setting.getId();

        UUID settingRecordId = userSettingRepository.save(UserSetting.builder()
                .user(user)
                .setting(setting)
                .value(true)
                .build()).getId();

        // Confirm initial value is true
        mockMvc.perform(get("/api/user-settings/" + settingRecordId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(true));

        // Create update request to set it to false
        UserSettingRequest updateRequest = UserSettingRequest.builder()
                .userId(user.getId())
                .settingId(settingId)
                .value(false)
                .build();

        // Perform PUT request
        mockMvc.perform(put("/api/user-settings/" + settingRecordId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(false));

        // Confirm value is now false
        mockMvc.perform(get("/api/user-settings/" + settingRecordId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(false));
    }

}
