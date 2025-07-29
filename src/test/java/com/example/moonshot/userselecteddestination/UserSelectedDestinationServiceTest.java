package com.example.moonshot.userselecteddestination;

import com.example.moonshot.auth.dto.LoginRequest;
import com.example.moonshot.country.Country;
import com.example.moonshot.country.CountryRepository;
import com.example.moonshot.datacleaner.TestDataCleaner;
import com.example.moonshot.destination.Destination;
import com.example.moonshot.destination.DestinationRepository;
import com.example.moonshot.user.User;
import com.example.moonshot.user.UserRepository;
import com.example.moonshot.userselecteddestination.dto.UserSelectedDestinationRequest;
import com.example.moonshot.userselecteddestination.dto.UserSelectedDestinationResponse;
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
class UserSelectedDestinationServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private DestinationRepository destinationRepository;
    @Autowired
    private UserSelectedDestinationRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    TestDataCleaner cleaner;

    private User user;
    private Destination destination;
    private String token;

    @BeforeEach
    void setup() throws Exception {
        cleaner.cleanAll();

        user = userRepository.save(User.builder()
                .name("Test User")
                .email("user@example.com")
                .passwordHash(passwordEncoder.encode("secret"))
                .platform("EMAIL")
                .build());

        Country france = countryRepository.save(Country.builder()
                .name("France")
                .available(true)
                .build());

        destination = destinationRepository.save(Destination.builder()
                .name("Louvre Museum")
                .type("Museum")
                .available(true)
                .country(france)
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
    void shouldCreateAndFetchUserSelectedDestination() throws Exception {
        UserSelectedDestinationRequest request = UserSelectedDestinationRequest.builder()
                .userId(user.getId())
                .destinationId(destination.getId())
                .build();

        MvcResult result = mockMvc.perform(post("/api/user-selected-destinations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.destinationName").value("Louvre Museum"))
                .andReturn();

        UserSelectedDestinationResponse created = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserSelectedDestinationResponse.class
        );

        mockMvc.perform(get("/api/user-selected-destinations/" + created.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(user.getId().toString()))
                .andExpect(jsonPath("$.destinationId").value(destination.getId().toString()));
    }

    @Test
    void shouldReturnAllUserSelectedDestinations() throws Exception {
        repository.save(UserSelectedDestination.builder()
                .user(user)
                .destination(destination)
                .build());

        mockMvc.perform(get("/api/user-selected-destinations")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldDeleteUserSelectedDestination() throws Exception {
        UserSelectedDestination saved = repository.save(UserSelectedDestination.builder()
                .user(user)
                .destination(destination)
                .build());

        UUID id = saved.getId();

        mockMvc.perform(delete("/api/user-selected-destinations/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/user-selected-destinations/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
