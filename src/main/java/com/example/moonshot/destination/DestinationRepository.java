package com.example.moonshot.destination;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, UUID> {
    Optional<Destination> findByName(String name);

    List<Destination> findAllByAvailableTrue();

    @Query("SELECT DISTINCT d.type FROM Destination d")
    List<String> findDistinctTypes();

}
