package com.example.moonshot.setting;

import com.example.moonshot.auth.dto.LoginRequest;
import com.example.moonshot.datacleaner.TestDataCleaner;
import com.example.moonshot.setting.dto.SettingRequest;
import com.example.moonshot.setting.dto.SettingResponse;
import com.example.moonshot.user.User;
import com.example.moonshot.user.UserRepository;
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
class SettingServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    TestDataCleaner cleaner;

    private String token;

    @BeforeEach
    void setup() throws Exception {
        cleaner.cleanAll();

        // Create test user and obtain token
        userRepository.save(User.builder()
                .email("user@example.com")
                .passwordHash(passwordEncoder.encode("secret"))
                .name("Test User")
                .platform("EMAIL")
                .build());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("user@example.com");
        loginRequest.setPassword("secret");

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        token = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void shouldCreateSettingAndFetchItById() throws Exception {
        SettingRequest request = SettingRequest.builder()
                .settingKey("notify_challenges")
                .label("Notify on challenge completion")
                .description("Send an alert when the user completes a challenge")
                .defaultValue(true)
                .build();

        MvcResult result = mockMvc.perform(post("/api/settings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.settingKey").value("notify_challenges"))
                .andExpect(jsonPath("$.defaultValue").value(true))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        SettingResponse created = objectMapper.readValue(body, SettingResponse.class);
        UUID id = created.getId();

        mockMvc.perform(get("/api/settings/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("Notify on challenge completion"))
                .andExpect(jsonPath("$.defaultValue").value(true));

        assertThat(created.getSettingKey()).isEqualTo("notify_challenges");
    }

    @Test
    void shouldReturnAllSettings() throws Exception {
        SettingRequest request = SettingRequest.builder()
                .settingKey("email_reminders")
                .label("Email Reminders")
                .description("Receive trip reminder emails")
                .defaultValue(false)
                .build();

        mockMvc.perform(post("/api/settings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/settings")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldDeleteSetting() throws Exception {
        Setting saved = settingRepository.save(Setting.builder()
                .settingKey("popup_alerts")
                .label("Popup Alerts")
                .description("Show popup notifications")
                .defaultValue(true)
                .build());

        UUID id = saved.getId();

        mockMvc.perform(delete("/api/settings/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/settings/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
