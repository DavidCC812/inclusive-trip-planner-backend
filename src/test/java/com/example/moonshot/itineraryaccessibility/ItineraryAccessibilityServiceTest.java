package com.example.moonshot.itineraryaccessibility;

import com.example.moonshot.accessibilityfeature.AccessibilityFeature;
import com.example.moonshot.accessibilityfeature.AccessibilityFeatureRepository;
import com.example.moonshot.auth.dto.LoginRequest;
import com.example.moonshot.country.Country;
import com.example.moonshot.country.CountryRepository;
import com.example.moonshot.datacleaner.TestDataCleaner;
import com.example.moonshot.destination.Destination;
import com.example.moonshot.destination.DestinationRepository;
import com.example.moonshot.itinerary.Itinerary;
import com.example.moonshot.itinerary.ItineraryRepository;
import com.example.moonshot.itineraryaccessibility.dto.ItineraryAccessibilityRequest;
import com.example.moonshot.itineraryaccessibility.dto.ItineraryAccessibilityResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ItineraryAccessibilityServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ItineraryRepository itineraryRepository;
    @Autowired
    private DestinationRepository destinationRepository;
    @Autowired
    private AccessibilityFeatureRepository featureRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    TestDataCleaner cleaner;

    private UUID itineraryId;
    private UUID featureId;
    private String token;

    @BeforeEach
    void setup() throws Exception {
        cleaner.cleanAll();

        Country france = countryRepository.save(Country.builder()
                .name("France")
                .available(true)
                .build());

        Destination destination = destinationRepository.save(Destination.builder()
                .name("Paris")
                .type("City")
                .available(true)
                .country(france)
                .build());

        Itinerary itinerary = itineraryRepository.save(Itinerary.builder()
                .title("Test Itinerary")
                .description("A test itinerary")
                .destination(destination)
                .price(BigDecimal.ZERO)
                .duration(1)
                .rating(4.5f)
                .build());

        AccessibilityFeature feature = featureRepository.save(AccessibilityFeature.builder()
                .name("Wheelchair Access")
                .build());

        itineraryId = itinerary.getId();
        featureId = feature.getId();

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
    void shouldCreateLinkAndFetchItById() throws Exception {
        ItineraryAccessibilityRequest request = ItineraryAccessibilityRequest.builder()
                .itineraryId(itineraryId)
                .featureId(featureId)
                .build();

        MvcResult result = mockMvc.perform(post("/api/itinerary-accessibility")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.itineraryId").value(itineraryId.toString()))
                .andExpect(jsonPath("$.featureId").value(featureId.toString()))
                .andReturn();

        ItineraryAccessibilityResponse created = objectMapper.readValue(result.getResponse().getContentAsString(), ItineraryAccessibilityResponse.class);

        mockMvc.perform(get("/api/itinerary-accessibility/" + created.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itineraryId").value(itineraryId.toString()))
                .andExpect(jsonPath("$.featureId").value(featureId.toString()));

        assertThat(created.getItineraryId()).isEqualTo(itineraryId);
        assertThat(created.getFeatureId()).isEqualTo(featureId);
    }

    @Test
    void shouldReturnAllAccessibilityLinksForItineraries() throws Exception {
        ItineraryAccessibilityRequest request = ItineraryAccessibilityRequest.builder()
                .itineraryId(itineraryId)
                .featureId(featureId)
                .build();

        mockMvc.perform(post("/api/itinerary-accessibility")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/itinerary-accessibility")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldDeleteAccessibilityLink() throws Exception {
        ItineraryAccessibilityRequest request = ItineraryAccessibilityRequest.builder()
                .itineraryId(itineraryId)
                .featureId(featureId)
                .build();

        MvcResult result = mockMvc.perform(post("/api/itinerary-accessibility")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        ItineraryAccessibilityResponse created = objectMapper.readValue(result.getResponse().getContentAsString(), ItineraryAccessibilityResponse.class);

        mockMvc.perform(delete("/api/itinerary-accessibility/" + created.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/itinerary-accessibility/" + created.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
