package com.example.moonshot.itinerarystep;

import com.example.moonshot.exception.MoonshotException;
import com.example.moonshot.itinerary.Itinerary;
import com.example.moonshot.itinerary.ItineraryRepository;
import com.example.moonshot.itinerarystep.dto.ItineraryStepRequest;
import com.example.moonshot.itinerarystep.dto.ItineraryStepResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ItineraryStepService {

    private final ItineraryStepRepository itineraryStepRepository;
    private final ItineraryRepository itineraryRepository;

    public ItineraryStepService(ItineraryStepRepository itineraryStepRepository,
                                ItineraryRepository itineraryRepository) {
        this.itineraryStepRepository = itineraryStepRepository;
        this.itineraryRepository = itineraryRepository;
    }

    public List<ItineraryStepResponse> getAll() {
        return itineraryStepRepository.findAll()
                .stream()
                .map(ItineraryStepResponse::from)
                .toList();
    }

    public ItineraryStep getById(UUID id) {
        return itineraryStepRepository.findById(id)
                .orElseThrow(() -> new MoonshotException("Itinerary step not found", HttpStatus.NOT_FOUND));
    }

    public List<ItineraryStepResponse> getByItineraryId(UUID itineraryId) {
        return itineraryStepRepository.findByItinerary_IdOrderByStepIndexAsc(itineraryId)
                .stream()
                .map(ItineraryStepResponse::from)
                .toList();
    }

    @Transactional
    public ItineraryStep create(ItineraryStepRequest request) {
        Itinerary itinerary = itineraryRepository.findById(request.getItineraryId())
                .orElseThrow(() -> new MoonshotException("Itinerary not found", HttpStatus.NOT_FOUND));

        ItineraryStep step = ItineraryStep.builder()
                .itinerary(itinerary)
                .stepIndex(request.getStepIndex())
                .title(request.getTitle())
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return itineraryStepRepository.save(step);
    }

    public void delete(UUID id) {
        itineraryStepRepository.deleteById(id);
    }
}
