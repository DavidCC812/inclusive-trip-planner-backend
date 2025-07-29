package com.example.moonshot.itinerarystep;

import com.example.moonshot.auth.dto.LoginRequest;
import com.example.moonshot.country.Country;
import com.example.moonshot.country.CountryRepository;
import com.example.moonshot.datacleaner.TestDataCleaner;
import com.example.moonshot.destination.Destination;
import com.example.moonshot.destination.DestinationRepository;
import com.example.moonshot.itinerary.Itinerary;
import com.example.moonshot.itinerary.ItineraryRepository;
import com.example.moonshot.itinerarystep.dto.ItineraryStepRequest;
import com.example.moonshot.itinerarystep.dto.ItineraryStepResponse;
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
class ItineraryStepServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DestinationRepository destinationRepository;
    @Autowired
    private ItineraryRepository itineraryRepository;
    @Autowired
    private ItineraryStepRepository itineraryStepRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    TestDataCleaner cleaner;

    private UUID itineraryId;
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
                .title("Paris Tour")
                .description("Best places in Paris")
                .price(BigDecimal.valueOf(80.00))
                .duration(2)
                .rating(4.5f)
                .destination(destination)
                .build());

        itineraryId = itinerary.getId();

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
    void shouldCreateItineraryStepAndFetchById() throws Exception {
        ItineraryStepRequest request = ItineraryStepRequest.builder()
                .itineraryId(itineraryId)
                .stepIndex(1)
                .title("Checkpoint Charlie")
                .description("Historic border crossing")
                .latitude(BigDecimal.valueOf(52.507))
                .longitude(BigDecimal.valueOf(13.390))
                .build();

        MvcResult result = mockMvc.perform(post("/api/itinerary-steps")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Checkpoint Charlie"))
                .andReturn();

        ItineraryStepResponse created = objectMapper.readValue(result.getResponse().getContentAsString(), ItineraryStepResponse.class);

        mockMvc.perform(get("/api/itinerary-steps/" + created.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Checkpoint Charlie"))
                .andExpect(jsonPath("$.itineraryId").value(itineraryId.toString()));

        assertThat(created.getItineraryId()).isEqualTo(itineraryId);
    }

    @Test
    void shouldReturnAllStepsForItinerary() throws Exception {
        for (int i = 1; i <= 2; i++) {
            ItineraryStepRequest request = ItineraryStepRequest.builder()
                    .itineraryId(itineraryId)
                    .stepIndex(i)
                    .title("Step " + i)
                    .description("Description " + i)
                    .latitude(BigDecimal.valueOf(52.5 + i))
                    .longitude(BigDecimal.valueOf(13.3 + i))
                    .build();

            mockMvc.perform(post("/api/itinerary-steps")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/itinerary-steps")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldDeleteItineraryStep() throws Exception {
        ItineraryStepRequest request = ItineraryStepRequest.builder()
                .itineraryId(itineraryId)
                .stepIndex(1)
                .title("Delete Me")
                .description("Temporary step")
                .latitude(BigDecimal.valueOf(52.0))
                .longitude(BigDecimal.valueOf(13.0))
                .build();

        MvcResult result = mockMvc.perform(post("/api/itinerary-steps")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        ItineraryStepResponse created = objectMapper.readValue(result.getResponse().getContentAsString(), ItineraryStepResponse.class);
        UUID stepId = created.getId();

        mockMvc.perform(delete("/api/itinerary-steps/" + stepId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/itinerary-steps/" + stepId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnStepsByItineraryId() throws Exception {
        for (int i = 1; i <= 3; i++) {
            ItineraryStepRequest request = ItineraryStepRequest.builder()
                    .itineraryId(itineraryId)
                    .stepIndex(i)
                    .title("Step " + i)
                    .description("Description for Step " + i)
                    .latitude(BigDecimal.valueOf(52.5 + i))
                    .longitude(BigDecimal.valueOf(13.3 + i))
                    .build();

            mockMvc.perform(post("/api/itinerary-steps")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/itinerary-steps/by-itinerary/" + itineraryId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].title").value("Step 1"))
                .andExpect(jsonPath("$[1].title").value("Step 2"))
                .andExpect(jsonPath("$[2].title").value("Step 3"));
    }
}
