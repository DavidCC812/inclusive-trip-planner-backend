package com.example.moonshot.itinerary;

import com.example.moonshot.auth.dto.LoginRequest;
import com.example.moonshot.country.Country;
import com.example.moonshot.country.CountryRepository;
import com.example.moonshot.datacleaner.TestDataCleaner;
import com.example.moonshot.destination.Destination;
import com.example.moonshot.destination.DestinationRepository;
import com.example.moonshot.itinerary.dto.ItineraryRequest;
import com.example.moonshot.itinerary.dto.ItineraryResponse;
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

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc

public class ItineraryServiceTest {

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

    private UUID destinationId;
    private String token;

    @BeforeEach
    void setup() throws Exception {
        cleaner.cleanAll();

        Country france = countryRepository.save(Country.builder()
                .name("France")
                .available(true)
                .build());

        Destination destination = destinationRepository.save(Destination.builder()
                .name("Louvre Museum")
                .type("Museum")
                .available(true)
                .country(france)
                .build());

        destinationId = destination.getId();

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
    void shouldCreateItineraryAndFetchItById() throws Exception {
        ItineraryRequest request = ItineraryRequest.builder()
                .title("Cultural Tour")
                .description("Explore the Louvre and surroundings")
                .price(BigDecimal.valueOf(100.00))
                .duration(2)
                .rating(4.5f)
                .destinationId(destinationId)
                .build();

        MvcResult result = mockMvc.perform(post("/api/itineraries")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Cultural Tour"))
                .andReturn();

        ItineraryResponse created = objectMapper.readValue(result.getResponse().getContentAsString(), ItineraryResponse.class);
        UUID itineraryId = created.getId();

        mockMvc.perform(get("/api/itineraries/" + itineraryId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Explore the Louvre and surroundings"))
                .andExpect(jsonPath("$.destinationId").value(destinationId.toString()));
    }

    @Test
    void shouldReturnAllItineraries() throws Exception {
        for (int i = 1; i <= 2; i++) {
            ItineraryRequest request = ItineraryRequest.builder()
                    .title("Tour " + i)
                    .description("Description " + i)
                    .price(BigDecimal.valueOf(80.00 + i))
                    .duration(i)
                    .rating(4.0f + i)
                    .destinationId(destinationId)
                    .build();

            mockMvc.perform(post("/api/itineraries")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/itineraries")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldDeleteItinerary() throws Exception {
        ItineraryRequest request = ItineraryRequest.builder()
                .title("Delete Tour")
                .description("Temporary itinerary")
                .price(BigDecimal.valueOf(50.00))
                .duration(1)
                .rating(3.5f)
                .destinationId(destinationId)
                .build();

        MvcResult result = mockMvc.perform(post("/api/itineraries")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        ItineraryResponse created = objectMapper.readValue(result.getResponse().getContentAsString(), ItineraryResponse.class);
        UUID itineraryId = created.getId();

        mockMvc.perform(delete("/api/itineraries/" + itineraryId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/itineraries/" + itineraryId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
