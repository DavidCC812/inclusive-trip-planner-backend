package com.example.moonshot.useraccessibilityfeature;

import com.example.moonshot.accessibilityfeature.AccessibilityFeature;
import com.example.moonshot.accessibilityfeature.AccessibilityFeatureRepository;
import com.example.moonshot.exception.MoonshotException;
import com.example.moonshot.user.User;
import com.example.moonshot.user.UserRepository;
import com.example.moonshot.useraccessibilityfeature.dto.UserAccessibilityFeatureRequest;
import com.example.moonshot.useraccessibilityfeature.dto.UserAccessibilityFeatureResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserAccessibilityFeatureService {

    private final UserAccessibilityFeatureRepository repository;
    private final UserRepository userRepository;
    private final AccessibilityFeatureRepository featureRepository;

    public UserAccessibilityFeatureService(
            UserAccessibilityFeatureRepository repository,
            UserRepository userRepository,
            AccessibilityFeatureRepository featureRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.featureRepository = featureRepository;
    }

    public List<UserAccessibilityFeatureResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(UserAccessibilityFeatureResponse::from)
                .collect(Collectors.toList());
    }

    public UserAccessibilityFeature getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new MoonshotException("UserAccessibilityFeature not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public UserAccessibilityFeature create(UserAccessibilityFeatureRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new MoonshotException("User not found", HttpStatus.NOT_FOUND));

        AccessibilityFeature feature = featureRepository.findById(request.getFeatureId())
                .orElseThrow(() -> new MoonshotException("Accessibility feature not found", HttpStatus.NOT_FOUND));

        UserAccessibilityFeature entity = UserAccessibilityFeature.builder()
                .user(user)
                .feature(feature)
                .build();

        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
