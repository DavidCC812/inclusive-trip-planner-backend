package com.example.moonshot.user;

import com.example.moonshot.exception.MoonshotException;
import com.example.moonshot.user.dto.UserRequest;
import com.example.moonshot.user.dto.UserResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new MoonshotException("User not found", HttpStatus.NOT_FOUND));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new MoonshotException("User not found by email", HttpStatus.NOT_FOUND));
    }

    public User getUserByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new MoonshotException("User not found by phone", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public User createUser(UserRequest dto) {
        String platform = dto.getPlatform() != null ? dto.getPlatform().toUpperCase() : "EMAIL";

        if (platform.equals("EMAIL")) {
            if (dto.getPasswordHash() == null || dto.getPasswordHash().isBlank()) {
                throw new IllegalArgumentException("Password is required for EMAIL platform users");
            }
        } else {
            if (dto.getPasswordHash() != null && !dto.getPasswordHash().isBlank()) {
                throw new IllegalArgumentException("OAuth users must not provide a password");
            }
        }

        User.UserBuilder builder = User.builder()
                .name(dto.getName())
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .platform(platform)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now());

        if (platform.equals("EMAIL")) {
            builder.passwordHash(passwordEncoder.encode(dto.getPasswordHash()));
        }

        User user = builder.build();

        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

}
