package com.example.moonshot.country;

import com.example.moonshot.country.dto.CountryRequest;
import com.example.moonshot.country.dto.CountryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/countries")
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping
    public List<CountryResponse> getAllCountries() {
        return countryService.getAllCountries();
    }

    @GetMapping("/{id}")
    public CountryResponse getCountryById(@PathVariable UUID id) {
        Country country = countryService.getCountryById(id);
        return CountryResponse.from(country);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CountryResponse createCountry(@RequestBody CountryRequest request) {
        Country country = countryService.createCountry(request);
        return CountryResponse.from(country);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCountry(@PathVariable UUID id) {
        countryService.deleteCountry(id);
    }
}
