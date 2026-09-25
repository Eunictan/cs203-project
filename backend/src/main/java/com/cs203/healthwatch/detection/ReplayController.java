package com.cs203.healthwatch.detection;

import com.cs203.healthwatch.events.DetectedEvent;
import com.cs203.healthwatch.events.EventRepository;
import com.cs203.healthwatch.events.EventStatus;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
@RequiredArgsConstructor
public class ReplayController {

    private final DetectionOrchestrator detectionOrchestrator;
    private final EventRepository eventRepository;
    // TODO once S1-4's ReadingRepository exists:
    // private final ReadingRepository readingRepository;

    @PostMapping("/replay")
    @PreAuthorize("hasRole('ADMIN')")
    public String replay() {
        String region = "north";
        String signalType = "AQI";

        // TODO: replace with real inserts once ReadingRepository exists.
        // Fixture sequence: 3 sustained high readings that should trip detection
        // given the test baseline (median 50, scaledMad 10) — mirrors your unit test.
        insertFixtureReading(region, signalType, 120);
        insertFixtureReading(region, signalType, 125);
        insertFixtureReading(region, signalType, 130);

        detectionOrchestrator.runDetection(region, signalType);

        // tag whatever event just got created as a replay, so it's never
        // mistaken for a live detection (Task 8's requirement)
        eventRepository.findFirstByRegionAndSignalTypeAndStatusIn(
                region, signalType, List.of(EventStatus.NEW)
        ).ifPresent(event -> {
            event.setReplay(true);
            eventRepository.save(event);
        });

        return "replay complete";
    }

    private void insertFixtureReading(String region, String signalType, double value) {
        // TODO: call readingRepository.save(...) once it exists, with
        // is_synthetic = true since this is fabricated demo data, not a real haze episode
        throw new UnsupportedOperationException("wire this to S1-4's reading repo");
    }
}