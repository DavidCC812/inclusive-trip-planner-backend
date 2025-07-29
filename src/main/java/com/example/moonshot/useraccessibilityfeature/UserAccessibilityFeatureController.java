package com.example.moonshot.useraccessibilityfeature;

import com.example.moonshot.useraccessibilityfeature.dto.UserAccessibilityFeatureRequest;
import com.example.moonshot.useraccessibilityfeature.dto.UserAccessibilityFeatureResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-accessibility-features")
public class UserAccessibilityFeatureController {

    private final UserAccessibilityFeatureService service;

    public UserAccessibilityFeatureController(UserAccessibilityFeatureService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserAccessibilityFeatureResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public UserAccessibilityFeatureResponse getById(@PathVariable UUID id) {
        UserAccessibilityFeature entity = service.getById(id);
        return UserAccessibilityFeatureResponse.from(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserAccessibilityFeatureResponse create(@RequestBody UserAccessibilityFeatureRequest request) {
        UserAccessibilityFeature created = service.create(request);
        return UserAccessibilityFeatureResponse.from(created);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
