package com.example.moonshot.userselecteddestination;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSelectedDestinationRepository extends JpaRepository<UserSelectedDestination, UUID> {
    List<UserSelectedDestination> findAllByUserId(UUID userId);
    Optional<UserSelectedDestination> findByUserIdAndDestinationId(UUID userId, UUID destinationId);
}
