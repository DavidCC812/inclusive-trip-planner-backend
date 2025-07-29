package com.example.moonshot.itineraryaccessibility;

import com.example.moonshot.accessibilityfeature.AccessibilityFeature;
import com.example.moonshot.accessibilityfeature.AccessibilityFeatureRepository;
import com.example.moonshot.exception.MoonshotException;
import com.example.moonshot.itinerary.Itinerary;
import com.example.moonshot.itinerary.ItineraryRepository;
import com.example.moonshot.itineraryaccessibility.dto.ItineraryAccessibilityRequest;
import com.example.moonshot.itineraryaccessibility.dto.ItineraryAccessibilityResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ItineraryAccessibilityService {

    private final ItineraryAccessibilityRepository repository;
    private final ItineraryRepository itineraryRepository;
    private final AccessibilityFeatureRepository featureRepository;

    public ItineraryAccessibilityService(
            ItineraryAccessibilityRepository repository,
            ItineraryRepository itineraryRepository,
            AccessibilityFeatureRepository featureRepository) {
        this.repository = repository;
        this.itineraryRepository = itineraryRepository;
        this.featureRepository = featureRepository;
    }

    public List<ItineraryAccessibilityResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(ItineraryAccessibilityResponse::from)
                .collect(Collectors.toList());
    }

    public ItineraryAccessibility getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new MoonshotException("ItineraryAccessibility relation not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public ItineraryAccessibility create(ItineraryAccessibilityRequest request) {
        Itinerary itinerary = itineraryRepository.findById(request.getItineraryId())
                .orElseThrow(() -> new MoonshotException("Itinerary not found", HttpStatus.NOT_FOUND));

        AccessibilityFeature feature = featureRepository.findById(request.getFeatureId())
                .orElseThrow(() -> new MoonshotException("Accessibility feature not found", HttpStatus.NOT_FOUND));

        ItineraryAccessibility entity = ItineraryAccessibility.builder()
                .itinerary(itinerary)
                .feature(feature)
                .build();

        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
