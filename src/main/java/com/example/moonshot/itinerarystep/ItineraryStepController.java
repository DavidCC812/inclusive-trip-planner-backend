package com.example.moonshot.itinerarystep;

import com.example.moonshot.itinerarystep.dto.ItineraryStepRequest;
import com.example.moonshot.itinerarystep.dto.ItineraryStepResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/itinerary-steps")
public class ItineraryStepController {

    private final ItineraryStepService itineraryStepService;

    public ItineraryStepController(ItineraryStepService itineraryStepService) {
        this.itineraryStepService = itineraryStepService;
    }

    @GetMapping
    public List<ItineraryStepResponse> getAll() {
        return itineraryStepService.getAll();
    }

    @GetMapping("/{id}")
    public ItineraryStepResponse getById(@PathVariable UUID id) {
        ItineraryStep step = itineraryStepService.getById(id);
        return ItineraryStepResponse.from(step);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItineraryStepResponse create(@RequestBody ItineraryStepRequest request) {
        ItineraryStep created = itineraryStepService.create(request);
        return ItineraryStepResponse.from(created);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        itineraryStepService.delete(id);
    }

    @GetMapping("/by-itinerary/{itineraryId}")
    public List<ItineraryStepResponse> getByItineraryId(@PathVariable UUID itineraryId) {
        return itineraryStepService.getByItineraryId(itineraryId);
    }
}
