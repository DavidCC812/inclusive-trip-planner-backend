package com.example.moonshot.usercountryaccess;

import com.example.moonshot.country.Country;
import com.example.moonshot.country.CountryRepository;
import com.example.moonshot.exception.MoonshotException;
import com.example.moonshot.user.User;
import com.example.moonshot.user.UserRepository;
import com.example.moonshot.usercountryaccess.dto.UserCountryAccessRequest;
import com.example.moonshot.usercountryaccess.dto.UserCountryAccessResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserCountryAccessService {

    private final UserCountryAccessRepository repository;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;

    public UserCountryAccessService(
            UserCountryAccessRepository repository,
            UserRepository userRepository,
            CountryRepository countryRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
    }

    public List<UserCountryAccessResponse> getAllAccesses() {
        return repository.findAll().stream()
                .map(UserCountryAccessResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserCountryAccess createAccess(UserCountryAccessRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new MoonshotException("User not found: " + request.getUserId(), HttpStatus.NOT_FOUND));

        Country country = countryRepository.findById(request.getCountryId())
                .orElseThrow(() -> new MoonshotException("Country not found: " + request.getCountryId(), HttpStatus.NOT_FOUND));

        UserCountryAccess access = UserCountryAccess.builder()
                .user(user)
                .country(country)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return repository.save(access);
    }

    public void deleteAccess(UUID id) {
        repository.deleteById(id);
    }
}
