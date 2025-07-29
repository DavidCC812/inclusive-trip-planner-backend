package com.example.moonshot.auth;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void shouldGenerateAndValidateToken() {
        UserDetails userDetails = User.withUsername("user@example.com")
                .password("password")
                .roles("USER")
                .build();

        String token = jwtService.generateToken(java.util.UUID.randomUUID(), userDetails.getUsername());

        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
        assertThat(jwtService.extractEmail(token)).isEqualTo("user@example.com");
    }

    @Test
    void shouldRejectExpiredToken() throws InterruptedException {
        UserDetails userDetails = User.withUsername("expired@example.com")
                .password("password")
                .roles("USER")
                .build();

        // Token that expires in 10 ms
        String token = jwtService.generateTokenWithExpiration(java.util.UUID.randomUUID(), userDetails.getUsername(), 10);

        // Wait until the token definitely expires
        Thread.sleep(50);

        // Assert that extracting claims from the expired token throws
        assertThatThrownBy(() -> jwtService.extractEmail(token))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
