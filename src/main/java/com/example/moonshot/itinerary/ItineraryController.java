package com.example.moonshot.itinerary;

import com.example.moonshot.itinerary.dto.ItineraryRequest;
import com.example.moonshot.itinerary.dto.ItineraryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/itineraries")
public class ItineraryController {

    private final ItineraryService itineraryService;

    public ItineraryController(ItineraryService itineraryService) {
        this.itineraryService = itineraryService;
    }

    @GetMapping
    public List<ItineraryResponse> getAll() {
        return itineraryService.getAllItineraries();
    }

    @GetMapping("/{id}")
    public ItineraryResponse getById(@PathVariable UUID id) {
        Itinerary itinerary = itineraryService.getItineraryById(id);
        return ItineraryResponse.from(itinerary);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItineraryResponse create(@RequestBody ItineraryRequest request) {
        Itinerary itinerary = itineraryService.createItinerary(request);
        return ItineraryResponse.from(itinerary);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        itineraryService.deleteItinerary(id);
    }
}
