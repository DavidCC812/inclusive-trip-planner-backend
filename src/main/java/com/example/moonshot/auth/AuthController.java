package com.example.moonshot.auth;

import com.example.moonshot.auth.dto.GoogleSignInRequest;
import com.example.moonshot.auth.dto.FacebookSignInRequest;
import com.example.moonshot.auth.dto.LoginRequest;
import com.example.moonshot.auth.dto.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody GoogleSignInRequest request) {
        try {
            LoginResponse response = authService.loginOrRegisterWithGoogle(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid Google token");
        }
    }

    @PostMapping("/facebook")
    public ResponseEntity<?> loginWithFacebook(@RequestBody FacebookSignInRequest request) {
        try {
            LoginResponse response = authService.loginOrRegisterWithFacebook(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid Facebook token");
        }
    }

}
