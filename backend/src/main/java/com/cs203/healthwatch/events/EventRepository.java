package com.cs203.healthwatch.events;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<DetectedEvent, UUID> {

    Optional<DetectedEvent> findFirstByRegionAndSignalTypeAndStatusIn(
            String region, String signalType, Iterable<EventStatus> openStatuses);
}