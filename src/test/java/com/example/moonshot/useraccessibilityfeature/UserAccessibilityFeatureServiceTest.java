package com.example.moonshot.useraccessibilityfeature;

import com.example.moonshot.accessibilityfeature.AccessibilityFeature;
import com.example.moonshot.accessibilityfeature.AccessibilityFeatureRepository;
import com.example.moonshot.auth.dto.LoginRequest;
import com.example.moonshot.user.User;
import com.example.moonshot.user.UserRepository;
import com.example.moonshot.useraccessibilityfeature.dto.UserAccessibilityFeatureRequest;
import com.example.moonshot.useraccessibilityfeature.dto.UserAccessibilityFeatureResponse;
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
class UserAccessibilityFeatureServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccessibilityFeatureRepository featureRepository;
    @Autowired
    private UserAccessibilityFeatureRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private UUID userId;
    private UUID featureId;
    private String token;

    @BeforeEach
    void setup() throws Exception {
        repository.deleteAll();
        featureRepository.deleteAll();
        userRepository.deleteAll();

        User user = userRepository.save(User.builder()
                .name("Inclusive Tester")
                .email("inclusion@example.com")
                .passwordHash(passwordEncoder.encode("test123"))
                .platform("EMAIL")
                .build());

        AccessibilityFeature feature = featureRepository.save(AccessibilityFeature.builder()
                .name("Sign Language Support")
                .build());

        userId = user.getId();
        featureId = feature.getId();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("inclusion@example.com");
        loginRequest.setPassword("test123");

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        token = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void shouldCreateLinkAndRetrieveItById() throws Exception {
        UserAccessibilityFeatureRequest request = UserAccessibilityFeatureRequest.builder()
                .userId(userId)
                .featureId(featureId)
                .build();

        MvcResult result = mockMvc.perform(post("/api/user-accessibility-features")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.featureId").value(featureId.toString()))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        UserAccessibilityFeatureResponse created = objectMapper.readValue(body, UserAccessibilityFeatureResponse.class);

        mockMvc.perform(get("/api/user-accessibility-features/" + created.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.featureId").value(featureId.toString()));

        assertThat(created.getUserId()).isEqualTo(userId);
        assertThat(created.getFeatureId()).isEqualTo(featureId);
    }

    @Test
    void shouldReturnAllUserAccessibilityFeatures() throws Exception {
        UserAccessibilityFeatureRequest request = UserAccessibilityFeatureRequest.builder()
                .userId(userId)
                .featureId(featureId)
                .build();

        mockMvc.perform(post("/api/user-accessibility-features")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/user-accessibility-features")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldDeleteUserAccessibilityFeature() throws Exception {
        UserAccessibilityFeature saved = repository.save(UserAccessibilityFeature.builder()
                .user(userRepository.findById(userId).orElseThrow())
                .feature(featureRepository.findById(featureId).orElseThrow())
                .build());

        mockMvc.perform(delete("/api/user-accessibility-features/" + saved.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/user-accessibility-features/" + saved.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
