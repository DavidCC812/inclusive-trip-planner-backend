package com.example.moonshot.itinerary;

import com.example.moonshot.auth.dto.LoginRequest;
import com.example.moonshot.country.Country;
import com.example.moonshot.country.CountryRepository;
import com.example.moonshot.datacleaner.TestDataCleaner;
import com.example.moonshot.destination.Destination;
import com.example.moonshot.destination.DestinationRepository;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ItinerarySecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private DestinationRepository destinationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TestDataCleaner cleaner;


    private UUID destinationId;

    @BeforeEach
    void setup() {
        cleaner.cleanAll();

        // Setup required entities
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
    }

    private String loginAndGetToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("user@example.com");
        loginRequest.setPassword("secret");

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void shouldDenyAccessWithoutToken() throws Exception {
        mockMvc.perform(get("/api/itineraries"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldDenyAccessWithInvalidToken() throws Exception {
        String fakeJwt = "eyJhbGciOiJIUzI1NiJ9.fakePayload.fakeSignature";

        mockMvc.perform(get("/api/itineraries")
                        .header("Authorization", "Bearer " + fakeJwt))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void shouldAllowAccessWithValidToken() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(get("/api/itineraries")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldAllowCreatingItineraryWithValidToken() throws Exception {
        String token = loginAndGetToken();

        String payload = """
                    {
                      "title": "Spring Boot Test Trip",
                      "description": "Created from integration test",
                      "price": 80.00,
                      "duration": 2,
                      "rating": 4.2,
                      "destinationId": "%s"
                    }
                """.formatted(destinationId);

        mockMvc.perform(post("/api/itineraries")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Spring Boot Test Trip"));
    }
}
