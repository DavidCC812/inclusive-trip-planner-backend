package com.example.moonshot.saveditinerary;

import com.example.moonshot.saveditinerary.dto.SavedItineraryRequest;
import com.example.moonshot.saveditinerary.dto.SavedItineraryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/saved-itineraries")
public class SavedItineraryController {

    private final SavedItineraryService service;

    public SavedItineraryController(SavedItineraryService service) {
        this.service = service;
    }

    @GetMapping
    public List<SavedItineraryResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public SavedItineraryResponse getById(@PathVariable UUID id) {
        SavedItinerary saved = service.getById(id);
        return SavedItineraryResponse.from(saved);
    }

    @GetMapping("/user/{userId}")
    public List<SavedItineraryResponse> getByUserId(@PathVariable UUID userId) {
        return service.getByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SavedItineraryResponse create(@RequestBody SavedItineraryRequest request) {
        SavedItinerary saved = service.create(request);
        return SavedItineraryResponse.from(saved);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
