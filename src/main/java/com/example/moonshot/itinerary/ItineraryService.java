package com.example.moonshot.itinerary;

import com.example.moonshot.destination.Destination;
import com.example.moonshot.destination.DestinationRepository;
import com.example.moonshot.exception.MoonshotException;
import com.example.moonshot.itinerary.dto.ItineraryRequest;
import com.example.moonshot.itinerary.dto.ItineraryResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ItineraryService {

    private final ItineraryRepository itineraryRepository;
    private final DestinationRepository destinationRepository;

    public ItineraryService(ItineraryRepository itineraryRepository, DestinationRepository destinationRepository) {
        this.itineraryRepository = itineraryRepository;
        this.destinationRepository = destinationRepository;
    }

    public List<ItineraryResponse> getAllItineraries() {
        return itineraryRepository.findAll()
                .stream()
                .map(ItineraryResponse::from)
                .collect(Collectors.toList());
    }

    public Itinerary getItineraryById(UUID id) {
        return itineraryRepository.findById(id)
                .orElseThrow(() -> new MoonshotException("Itinerary not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Itinerary createItinerary(ItineraryRequest request) {
        Destination destination = destinationRepository.findById(request.getDestinationId())
                .orElseThrow(() -> new MoonshotException("Destination not found", HttpStatus.NOT_FOUND));

        Itinerary itinerary = Itinerary.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .duration(request.getDuration())
                .rating(request.getRating())
                .destination(destination)
                .imageUrl(request.getImageUrl())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return itineraryRepository.save(itinerary);
    }

    public void deleteItinerary(UUID id) {
        itineraryRepository.deleteById(id);
    }
}
