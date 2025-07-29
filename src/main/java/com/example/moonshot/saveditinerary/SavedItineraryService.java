package com.example.moonshot.saveditinerary;

import com.example.moonshot.exception.MoonshotException;
import com.example.moonshot.itinerary.Itinerary;
import com.example.moonshot.itinerary.ItineraryRepository;
import com.example.moonshot.saveditinerary.dto.SavedItineraryRequest;
import com.example.moonshot.saveditinerary.dto.SavedItineraryResponse;
import com.example.moonshot.user.User;
import com.example.moonshot.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SavedItineraryService {

    private final SavedItineraryRepository repository;
    private final UserRepository userRepository;
    private final ItineraryRepository itineraryRepository;

    public SavedItineraryService(
            SavedItineraryRepository repository,
            UserRepository userRepository,
            ItineraryRepository itineraryRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.itineraryRepository = itineraryRepository;
    }

    public List<SavedItineraryResponse> getAll() {
        return repository.findAll().stream()
                .map(SavedItineraryResponse::from)
                .collect(Collectors.toList());
    }

    public SavedItinerary getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new MoonshotException("Saved itinerary not found", HttpStatus.NOT_FOUND));
    }

    public List<SavedItineraryResponse> getByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(SavedItineraryResponse::from)
                .toList();
    }

    @Transactional
    public SavedItinerary create(SavedItineraryRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new MoonshotException("User not found", HttpStatus.NOT_FOUND));

        Itinerary itinerary = itineraryRepository.findById(request.getItineraryId())
                .orElseThrow(() -> new MoonshotException("Itinerary not found", HttpStatus.NOT_FOUND));

        SavedItinerary saved = SavedItinerary.builder()
                .user(user)
                .itinerary(itinerary)
                .build();

        return repository.save(saved);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
