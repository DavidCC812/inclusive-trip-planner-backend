package com.example.moonshot.saveditinerary;

import com.example.moonshot.auth.dto.LoginRequest;
import com.example.moonshot.country.Country;
import com.example.moonshot.country.CountryRepository;
import com.example.moonshot.datacleaner.TestDataCleaner;
import com.example.moonshot.destination.Destination;
import com.example.moonshot.destination.DestinationRepository;
import com.example.moonshot.itinerary.Itinerary;
import com.example.moonshot.itinerary.ItineraryRepository;
import com.example.moonshot.saveditinerary.dto.SavedItineraryRequest;
import com.example.moonshot.saveditinerary.dto.SavedItineraryResponse;
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
class SavedItineraryServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SavedItineraryRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItineraryRepository itineraryRepository;
    @Autowired
    private DestinationRepository destinationRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    TestDataCleaner cleaner;

    private User user;
    private Itinerary itinerary;
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

        Destination destination = destinationRepository.save(Destination.builder()
                .name("Paris")
                .type("Historical")
                .available(true)
                .country(france)
                .build());

        itinerary = itineraryRepository.save(Itinerary.builder()
                .title("Paris Adventure")
                .description("Visit the Louvre and more")
                .destination(destination)
                .price(new BigDecimal("99.99"))
                .duration(2)
                .rating(4.6f)
                .build());

        // login to get JWT token
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
    void shouldCreateAndFetchSavedItineraryById() throws Exception {
        SavedItineraryRequest request = SavedItineraryRequest.builder()
                .userId(user.getId())
                .itineraryId(itinerary.getId())
                .build();

        MvcResult result = mockMvc.perform(post("/api/saved-itineraries")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        SavedItineraryResponse created = objectMapper.readValue(body, SavedItineraryResponse.class);

        mockMvc.perform(get("/api/saved-itineraries/" + created.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(user.getId().toString()))
                .andExpect(jsonPath("$.itineraryId").value(itinerary.getId().toString()));

        assertThat(created.getUserId()).isEqualTo(user.getId());
        assertThat(created.getItineraryId()).isEqualTo(itinerary.getId());
    }

    @Test
    void shouldReturnAllSavedItineraries() throws Exception {
        SavedItineraryRequest request = SavedItineraryRequest.builder()
                .userId(user.getId())
                .itineraryId(itinerary.getId())
                .build();

        mockMvc.perform(post("/api/saved-itineraries")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/saved-itineraries")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldDeleteSavedItinerary() throws Exception {
        SavedItinerary saved = repository.save(SavedItinerary.builder()
                .user(user)
                .itinerary(itinerary)
                .build());

        UUID id = saved.getId();

        mockMvc.perform(delete("/api/saved-itineraries/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/saved-itineraries/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
