package com.example.moonshot.auth;

import com.example.moonshot.auth.dto.GoogleSignInRequest;
import com.example.moonshot.auth.dto.FacebookSignInRequest;
import com.example.moonshot.auth.dto.LoginRequest;
import com.example.moonshot.auth.dto.LoginResponse;
import com.example.moonshot.user.User;
import com.example.moonshot.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final FirebaseTokenVerifier firebaseTokenVerifier;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);


    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            FirebaseTokenVerifier firebaseTokenVerifier
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.firebaseTokenVerifier = firebaseTokenVerifier;
    }



    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getIdentifier())
                .or(() -> userRepository.findByPhone(request.getIdentifier()))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid password");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        return LoginResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .token(token)
                .build();
    }

    private LoginResponse loginOrRegisterWithFirebasePayload(Map<String, Object> payload, String idToken, String platform) {
        String email = (String) payload.get("email");
        String name = (String) payload.get("name");

        Optional<User> existingUser = userRepository.findByEmail(email);

        User user = existingUser.map(u -> {
            u.setOauthToken(idToken);
            u.setPlatform(platform);
            return userRepository.save(u);
        }).orElseGet(() -> {
            User newUser = User.builder()
                    .name(name)
                    .email(email)
                    .platform(platform)
                    .oauthToken(idToken)
                    .build();
            return userRepository.save(newUser);
        });

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        return LoginResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .token(token)
                .build();
    }


    public LoginResponse loginOrRegisterWithGoogle(GoogleSignInRequest request) {
        Map<String, Object> payload = firebaseTokenVerifier.verify(request.getIdToken());
        return loginOrRegisterWithFirebasePayload(payload, request.getIdToken(), "GOOGLE");
    }

    public LoginResponse loginOrRegisterWithFacebook(FacebookSignInRequest request) {
        log.info("Received Facebook token: {}", request.getIdToken());
        try {
            Map<String, Object> payload = firebaseTokenVerifier.verify(request.getIdToken());
            log.info("Token verified successfully: {}", payload);
            return loginOrRegisterWithFirebasePayload(payload, request.getIdToken(), "FACEBOOK");
        } catch (Exception e) {
            log.error("Token verification failed", e);
            throw new IllegalArgumentException("Invalid Facebook token");
        }
    }

}
