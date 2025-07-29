package com.example.moonshot.review;

import com.example.moonshot.auth.dto.LoginRequest;
import com.example.moonshot.country.Country;
import com.example.moonshot.country.CountryRepository;
import com.example.moonshot.datacleaner.TestDataCleaner;
import com.example.moonshot.destination.Destination;
import com.example.moonshot.destination.DestinationRepository;
import com.example.moonshot.itinerary.Itinerary;
import com.example.moonshot.itinerary.ItineraryRepository;
import com.example.moonshot.review.dto.ReviewRequest;
import com.example.moonshot.review.dto.ReviewResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ReviewServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DestinationRepository destinationRepository;
    @Autowired
    private ItineraryRepository itineraryRepository;
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
                .name("Alice")
                .email("alice@example.com")
                .passwordHash(passwordEncoder.encode("secret"))
                .phone("123456789")
                .platform("EMAIL")
                .build());

        Country france = countryRepository.save(Country.builder()
                .name("France")
                .available(true)
                .build());

        Destination destination = destinationRepository.save(Destination.builder()
                .name("Paris")
                .type("Historic")
                .available(true)
                .country(france)
                .build());

        itinerary = itineraryRepository.save(Itinerary.builder()
                .title("Paris in a Day")
                .description("Visit the top spots in Paris")
                .price(BigDecimal.valueOf(35.0))
                .duration(1)
                .rating(4.7f)
                .destination(destination)
                .build());

        // Perform login to get JWT token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("alice@example.com");
        loginRequest.setPassword("secret");

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        token = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void shouldCreateReviewAndFetchItById() throws Exception {
        ReviewRequest request = ReviewRequest.builder()
                .userId(user.getId())
                .itineraryId(itinerary.getId())
                .rating(4.5f)
                .comment("Amazing experience!")
                .build();

        MvcResult result = mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(user.getId().toString()))
                .andExpect(jsonPath("$.itineraryId").value(itinerary.getId().toString()))
                .andExpect(jsonPath("$.rating").value(4.5))
                .andExpect(jsonPath("$.comment").value("Amazing experience!"))
                .andReturn();

        ReviewResponse created = objectMapper.readValue(result.getResponse().getContentAsString(), ReviewResponse.class);

        mockMvc.perform(get("/api/reviews/" + created.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(user.getId().toString()))
                .andExpect(jsonPath("$.itineraryId").value(itinerary.getId().toString()))
                .andExpect(jsonPath("$.comment").value("Amazing experience!"));

        assertThat(created.getUserId()).isEqualTo(user.getId());
        assertThat(created.getItineraryId()).isEqualTo(itinerary.getId());
    }

    @Test
    void shouldReturnAllReviews() throws Exception {
        ReviewRequest request = ReviewRequest.builder()
                .userId(user.getId())
                .itineraryId(itinerary.getId())
                .rating(4.0f)
                .comment("Great trip")
                .build();

        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/reviews")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldDeleteReview() throws Exception {
        Review review = reviewRepository.save(Review.builder()
                .user(user)
                .itinerary(itinerary)
                .rating(3.5f)
                .comment("Temporary review")
                .build());

        mockMvc.perform(delete("/api/reviews/" + review.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/reviews/" + review.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
