package com.example.moonshot.destination;

import com.example.moonshot.destination.dto.DestinationRequest;
import com.example.moonshot.destination.dto.DestinationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/destinations")
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @GetMapping
    public List<DestinationResponse> getAllDestinations() {
        return destinationService.getAllDestinations();
    }

    @GetMapping("/available")
    public List<DestinationResponse> getAvailableDestinations() {
        return destinationService.getAvailableDestinations();
    }

    @GetMapping("/{id}")
    public DestinationResponse getDestinationById(@PathVariable UUID id) {
        Destination destination = destinationService.getDestinationById(id);
        return DestinationResponse.from(destination);
    }

    @GetMapping("/types")
    public List<String> getDestinationTypes() {
        return destinationService.getAllDestinationTypes();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DestinationResponse createDestination(@RequestBody DestinationRequest request) {
        Destination destination = destinationService.createDestination(request);
        return DestinationResponse.from(destination);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDestination(@PathVariable UUID id) {
        destinationService.deleteDestination(id);
    }
}
