package com.example.moonshot.country;

import com.example.moonshot.country.dto.CountryRequest;
import com.example.moonshot.country.dto.CountryResponse;
import com.example.moonshot.exception.MoonshotException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public List<CountryResponse> getAllCountries() {
        return countryRepository.findAll()
                .stream()
                .map(CountryResponse::from)
                .collect(Collectors.toList());
    }

    public Country getCountryById(UUID id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new MoonshotException("Country not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Country createCountry(CountryRequest request) {
        Country country = Country.builder()
                .name(request.getName())
                .available(request.isAvailable())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return countryRepository.save(country);
    }

    public void deleteCountry(UUID id) {
        countryRepository.deleteById(id);
    }
}
