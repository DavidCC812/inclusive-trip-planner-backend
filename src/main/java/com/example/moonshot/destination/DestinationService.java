package com.example.moonshot.destination;

import com.example.moonshot.country.Country;
import com.example.moonshot.country.CountryRepository;
import com.example.moonshot.destination.dto.DestinationRequest;
import com.example.moonshot.destination.dto.DestinationResponse;
import com.example.moonshot.exception.MoonshotException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DestinationService {

    private final DestinationRepository destinationRepository;
    private final CountryRepository countryRepository;

    public DestinationService(DestinationRepository destinationRepository, CountryRepository countryRepository) {
        this.destinationRepository = destinationRepository;
        this.countryRepository = countryRepository;
    }

    public List<DestinationResponse> getAllDestinations() {
        return destinationRepository.findAll()
                .stream()
                .map(DestinationResponse::from)
                .collect(Collectors.toList());
    }

    public List<DestinationResponse> getAvailableDestinations() {
        return destinationRepository.findAllByAvailableTrue()
                .stream()
                .map(DestinationResponse::from)
                .toList();
    }

    public List<String> getAllDestinationTypes() {
        return destinationRepository.findDistinctTypes();
    }

    public Destination getDestinationById(UUID id) {
        return destinationRepository.findById(id)
                .orElseThrow(() -> new MoonshotException("Destination not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Destination createDestination(DestinationRequest dto) {
        Country country = countryRepository.findById(dto.getCountryId())
                .orElseThrow(() -> new MoonshotException("Country not found", HttpStatus.NOT_FOUND));

        Destination destination = Destination.builder()
                .name(dto.getName())
                .type(dto.getType())
                .available(dto.isAvailable())
                .country(country)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return destinationRepository.save(destination);
    }

    public void deleteDestination(UUID id) {
        destinationRepository.deleteById(id);
    }
}
