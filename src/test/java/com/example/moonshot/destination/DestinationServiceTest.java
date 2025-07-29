package com.example.moonshot.destination;

import com.example.moonshot.auth.dto.LoginRequest;
import com.example.moonshot.country.Country;
import com.example.moonshot.country.CountryRepository;
import com.example.moonshot.datacleaner.TestDataCleaner;
import com.example.moonshot.destination.dto.DestinationRequest;
import com.example.moonshot.destination.dto.DestinationResponse;
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

public class DestinationServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DestinationRepository destinationRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    TestDataCleaner cleaner;

    private Country france;
    private String token;

    @BeforeEach
    void setup() throws Exception {
        cleaner.cleanAll();

        france = countryRepository.save(Country.builder()
                .name("France")
                .available(true)
                .build());

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
    void shouldCreateDestinationAndFetchItById() throws Exception {
        DestinationRequest requestDto = DestinationRequest.builder()
                .name("Tour Eiffel")
                .countryId(france.getId())
                .type("Monument")
                .available(true)
                .build();

        MvcResult result = mockMvc.perform(post("/api/destinations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Tour Eiffel"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.countryName").value("France"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        DestinationResponse created = objectMapper.readValue(responseBody, DestinationResponse.class);
        UUID destinationId = created.getId();

        mockMvc.perform(get("/api/destinations/" + destinationId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryName").value("France"))
                .andExpect(jsonPath("$.type").value("Monument"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void shouldReturnAllDestinations() throws Exception {
        DestinationRequest request = DestinationRequest.builder()
                .name("Louvre Museum")
                .countryId(france.getId())
                .type("Museum")
                .available(true)
                .build();

        mockMvc.perform(post("/api/destinations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/destinations")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldDeleteDestination() throws Exception {
        Destination destination = destinationRepository.save(Destination.builder()
                .name("Arc de Triomphe")
                .country(france)
                .type("Monument")
                .available(true)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build());

        UUID id = destination.getId();

        mockMvc.perform(delete("/api/destinations/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/destinations/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnOnlyAvailableDestinations() throws Exception {
        DestinationRequest available = DestinationRequest.builder()
                .name("Open City")
                .countryId(france.getId())
                .type("City")
                .available(true)
                .build();

        DestinationRequest unavailable = DestinationRequest.builder()
                .name("Hidden City")
                .countryId(france.getId())
                .type("City")
                .available(false)
                .build();

        mockMvc.perform(post("/api/destinations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(available)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/destinations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unavailable)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/destinations/available")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Open City"));
    }

    @Test
    void shouldReturnAllDestinationTypes() throws Exception {
        DestinationRequest museum = DestinationRequest.builder()
                .name("Louvre Museum")
                .type("Museum")
                .available(true)
                .countryId(france.getId())
                .build();

        DestinationRequest park = DestinationRequest.builder()
                .name("Luxembourg Garden")
                .type("Park")
                .available(true)
                .countryId(france.getId())
                .build();

        DestinationRequest theater = DestinationRequest.builder()
                .name("Opéra Garnier")
                .type("Theater")
                .available(true)
                .countryId(france.getId())
                .build();

        mockMvc.perform(post("/api/destinations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(museum)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/destinations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(park)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/destinations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(theater)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/destinations/types")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@ == 'Museum')]").exists())
                .andExpect(jsonPath("$[?(@ == 'Park')]").exists())
                .andExpect(jsonPath("$[?(@ == 'Theater')]").exists());
    }
}
