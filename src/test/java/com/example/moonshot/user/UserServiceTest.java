package com.example.moonshot.user;

import com.example.moonshot.auth.dto.LoginRequest;
import com.example.moonshot.datacleaner.TestDataCleaner;
import com.example.moonshot.user.dto.UserRequest;
import com.example.moonshot.user.dto.UserResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    TestDataCleaner cleaner;

    private String token;

    @BeforeEach
    void setup() throws Exception {
        // Clear everything using the centralized cleaner
        cleaner.cleanAll();

        userRepository.save(User.builder()
                .email("user@example.com")
                .passwordHash(passwordEncoder.encode("secret"))
                .name("Auth User")
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
    void shouldCreateUserAndFetchItById() throws Exception {
        UserRequest request = UserRequest.builder()
                .name("Alice")
                .email("alice@example.com")
                .passwordHash("secret")
                .phone("123456")
                .build();

        MvcResult result = mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andReturn();

        UserResponse created = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);

        mockMvc.perform(get("/api/users/" + created.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));

        assertThat(created.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        UserRequest request = UserRequest.builder()
                .name("Bob")
                .email("bob@example.com")
                .passwordHash("pass")
                .phone("987654")
                .build();

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)); // includes the logged-in user too
    }

    @Test
    void shouldDeleteUser() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Charlie")
                .email("charlie@example.com")
                .passwordHash("hash")
                .phone("000111")
                .platform("EMAIL")
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build());

        mockMvc.perform(delete("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
