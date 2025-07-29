package com.example.moonshot.accessibilityfeature;

import com.example.moonshot.accessibilityfeature.dto.AccessibilityFeatureRequest;
import com.example.moonshot.accessibilityfeature.dto.AccessibilityFeatureResponse;
import com.example.moonshot.exception.MoonshotException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccessibilityFeatureService {

    private final AccessibilityFeatureRepository featureRepository;

    public AccessibilityFeatureService(AccessibilityFeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    public List<AccessibilityFeatureResponse> getAllFeatures() {
        return featureRepository.findAll()
                .stream()
                .map(AccessibilityFeatureResponse::from)
                .collect(Collectors.toList());
    }


    public AccessibilityFeature getFeatureById(UUID id) {
        return featureRepository.findById(id)
                .orElseThrow(() -> new MoonshotException("Accessibility feature not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public AccessibilityFeature createFeature(AccessibilityFeatureRequest dto) {
        AccessibilityFeature feature = AccessibilityFeature.builder()
                .name(dto.getName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return featureRepository.save(feature);
    }

    public void deleteFeature(UUID id) {
        featureRepository.deleteById(id);
    }
}
