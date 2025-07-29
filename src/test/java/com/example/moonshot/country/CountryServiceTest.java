package com.example.moonshot.country;

import com.example.moonshot.auth.dto.LoginRequest;
import com.example.moonshot.country.dto.CountryRequest;
import com.example.moonshot.country.dto.CountryResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CountryServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CountryRepository countryRepository;
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

        // Setup a user and retrieve token
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
    void shouldCreateCountryAndFetchById() throws Exception {
        CountryRequest request = CountryRequest.builder()
                .name("France")
                .available(true)
                .build();

        MvcResult result = mockMvc.perform(post("/api/countries")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("France"))
                .andExpect(jsonPath("$.available").value(true))
                .andReturn();

        CountryResponse created = objectMapper.readValue(result.getResponse().getContentAsString(), CountryResponse.class);
        UUID countryId = created.getId();

        mockMvc.perform(get("/api/countries/" + countryId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("France"))
                .andExpect(jsonPath("$.available").value(true));

        assertThat(created.getName()).isEqualTo("France");
    }

    @Test
    void shouldReturnAllCountries() throws Exception {
        for (int i = 1; i <= 2; i++) {
            CountryRequest request = CountryRequest.builder()
                    .name("Country " + i)
                    .available(i % 2 == 0)
                    .build();

            mockMvc.perform(post("/api/countries")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/countries")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldDeleteCountry() throws Exception {
        Country country = countryRepository.save(Country.builder()
                .name("ToDelete")
                .available(true)
                .build());

        mockMvc.perform(delete("/api/countries/" + country.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/countries/" + country.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
