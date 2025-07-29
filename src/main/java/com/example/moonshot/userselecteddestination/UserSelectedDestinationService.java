package com.example.moonshot.userselecteddestination;

import com.example.moonshot.destination.Destination;
import com.example.moonshot.destination.DestinationRepository;
import com.example.moonshot.exception.MoonshotException;
import com.example.moonshot.user.User;
import com.example.moonshot.user.UserRepository;
import com.example.moonshot.userselecteddestination.dto.UserSelectedDestinationRequest;
import com.example.moonshot.userselecteddestination.dto.UserSelectedDestinationResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserSelectedDestinationService {

    private final UserSelectedDestinationRepository repository;
    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;

    public UserSelectedDestinationService(
            UserSelectedDestinationRepository repository,
            UserRepository userRepository,
            DestinationRepository destinationRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.destinationRepository = destinationRepository;
    }

    public List<UserSelectedDestinationResponse> getAll() {
        return repository.findAll().stream()
                .map(UserSelectedDestinationResponse::from)
                .collect(Collectors.toList());
    }

    public UserSelectedDestination getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new MoonshotException("UserSelectedDestination not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public UserSelectedDestination create(UserSelectedDestinationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new MoonshotException("User not found: " + request.getUserId(), HttpStatus.NOT_FOUND));

        Destination destination = destinationRepository.findById(request.getDestinationId())
                .orElseThrow(() -> new MoonshotException("Destination not found: " + request.getDestinationId(), HttpStatus.NOT_FOUND));

        UserSelectedDestination link = UserSelectedDestination.builder()
                .user(user)
                .destination(destination)
                .build();

        return repository.save(link);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
