package com.example.moonshot.auth;

import com.example.moonshot.auth.dto.LoginRequest;
import com.example.moonshot.datacleaner.TestDataCleaner;
import com.example.moonshot.user.User;
import com.example.moonshot.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TestDataCleaner testDataCleaner;


    @MockBean
    private FirebaseTokenVerifier firebaseTokenVerifier;


    @BeforeEach
    void setup() {
        testDataCleaner.cleanAll();


        userRepository.save(User.builder()
                .email("valid@example.com")
                .phone("1234567890")
                .name("Valid User")
                .passwordHash(passwordEncoder.encode("correctpassword"))
                .platform("EMAIL")
                .build());
    }

    @Test
    void shouldLoginSuccessfullyWithEmail() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setIdentifier("valid@example.com");
        request.setPassword("correctpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.email").value("valid@example.com"));
    }

    @Test
    void shouldLoginSuccessfullyWithPhone() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setIdentifier("1234567890");
        request.setPassword("correctpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("valid@example.com"));
    }

    @Test
    void shouldFailWithWrongPassword() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setIdentifier("valid@example.com");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWithUnknownEmail() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setIdentifier("unknown@example.com");
        request.setPassword("any");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateUserViaGoogleSignIn() throws Exception {
        String idToken = "fake-google-token";

        when(firebaseTokenVerifier.verify(idToken)).thenReturn(Map.of(
                "email", "google@example.com",
                "name", "Google User"
        ));

        mockMvc.perform(post("/api/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "idToken": "fake-google-token"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("google@example.com"))
                .andExpect(jsonPath("$.token").exists());

        User savedUser = userRepository.findByEmail("google@example.com").orElseThrow();
        assertEquals("GOOGLE", savedUser.getPlatform());
        assertEquals("fake-google-token", savedUser.getOauthToken());
    }

    @Test
    void shouldCreateUserViaFacebookSignIn() throws Exception {
        String idToken = "fake-fb-token";

        when(firebaseTokenVerifier.verify(idToken)).thenReturn(Map.of(
                "email", "facebook@example.com",
                "name", "Facebook User"
        ));

        mockMvc.perform(post("/api/auth/facebook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "idToken": "fake-fb-token"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("facebook@example.com"))
                .andExpect(jsonPath("$.token").exists());

        User savedUser = userRepository.findByEmail("facebook@example.com").orElseThrow();
        assertEquals("FACEBOOK", savedUser.getPlatform());
        assertEquals("fake-fb-token", savedUser.getOauthToken());
    }


}
