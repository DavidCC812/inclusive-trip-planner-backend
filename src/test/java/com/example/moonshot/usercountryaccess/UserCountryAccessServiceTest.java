package com.example.moonshot.usercountryaccess;

import com.example.moonshot.auth.dto.LoginRequest;
import com.example.moonshot.country.Country;
import com.example.moonshot.country.CountryRepository;
import com.example.moonshot.datacleaner.TestDataCleaner;
import com.example.moonshot.user.User;
import com.example.moonshot.user.UserRepository;
import com.example.moonshot.usercountryaccess.dto.UserCountryAccessRequest;
import com.example.moonshot.usercountryaccess.dto.UserCountryAccessResponse;
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
class UserCountryAccessServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private UserCountryAccessRepository userCountryAccessRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    TestDataCleaner cleaner;

    private User user;
    private Country france;
    private String token;

    @BeforeEach
    void setUp() throws Exception {
        cleaner.cleanAll();

        user = userRepository.save(User.builder()
                .name("Alice")
                .email("alice@example.com")
                .passwordHash(passwordEncoder.encode("secure"))
                .platform("EMAIL")
                .build());

        france = countryRepository.save(Country.builder()
                .name("France")
                .available(true)
                .build());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("alice@example.com");
        loginRequest.setPassword("secure");

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        token = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void shouldCreateAndFetchUserCountryAccess() throws Exception {
        UserCountryAccessRequest request = UserCountryAccessRequest.builder()
                .userId(user.getId())
                .countryId(france.getId())
                .build();

        MvcResult result = mockMvc.perform(post("/api/user-country-access")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(user.getId().toString()))
                .andExpect(jsonPath("$.countryId").value(france.getId().toString()))
                .andExpect(jsonPath("$.countryName").value("France"))
                .andReturn();

        UserCountryAccessResponse created = objectMapper.readValue(result.getResponse().getContentAsString(), UserCountryAccessResponse.class);
        assertThat(created.getUserId()).isEqualTo(user.getId());
        assertThat(created.getCountryName()).isEqualTo("France");
    }

    @Test
    void shouldReturnAllUserCountryAccesses() throws Exception {
        userCountryAccessRepository.save(UserCountryAccess.builder()
                .user(user)
                .country(france)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build());

        mockMvc.perform(get("/api/user-country-access")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(user.getId().toString()));
    }

    @Test
    void shouldDeleteUserCountryAccess() throws Exception {
        UserCountryAccess access = userCountryAccessRepository.save(UserCountryAccess.builder()
                .user(user)
                .country(france)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build());

        UUID id = access.getId();

        mockMvc.perform(delete("/api/user-country-access/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/user-country-access")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
