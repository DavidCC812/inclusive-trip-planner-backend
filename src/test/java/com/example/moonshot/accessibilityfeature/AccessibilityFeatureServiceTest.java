package com.example.moonshot.accessibilityfeature;

import com.example.moonshot.accessibilityfeature.dto.AccessibilityFeatureRequest;
import com.example.moonshot.accessibilityfeature.dto.AccessibilityFeatureResponse;
import com.example.moonshot.auth.dto.LoginRequest;
import com.example.moonshot.datacleaner.TestDataCleaner;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AccessibilityFeatureServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AccessibilityFeatureRepository featureRepository;
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

        userRepository.save(User.builder()
                .email("user@example.com")
                .passwordHash(passwordEncoder.encode("secret"))
                .name("Test User")
                .platform("email")
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
    void shouldCreateFeatureAndFetchItById() throws Exception {
        AccessibilityFeatureRequest request = AccessibilityFeatureRequest.builder()
                .name("Braille Signage")
                .build();

        MvcResult result = mockMvc.perform(post("/api/accessibility-features")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Braille Signage"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        AccessibilityFeatureResponse created = objectMapper.readValue(responseBody, AccessibilityFeatureResponse.class);
        UUID id = created.getId();

        mockMvc.perform(get("/api/accessibility-features/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Braille Signage"));
    }

    @Test
    void shouldReturnAllFeatures() throws Exception {
        AccessibilityFeatureRequest request = AccessibilityFeatureRequest.builder()
                .name("Visual Alerts")
                .build();

        mockMvc.perform(post("/api/accessibility-features")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/accessibility-features")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldDeleteFeature() throws Exception {
        AccessibilityFeature feature = featureRepository.save(AccessibilityFeature.builder()
                .name("Audio Descriptions")
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build());

        UUID id = feature.getId();

        mockMvc.perform(delete("/api/accessibility-features/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/accessibility-features/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
