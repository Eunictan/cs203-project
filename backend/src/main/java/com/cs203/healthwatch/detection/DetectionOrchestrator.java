package com.cs203.healthwatch.detection;

import com.cs203.healthwatch.events.*;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DetectionOrchestrator {

    private final EventRepository eventRepository;
    private final DetectionProperties config;
    // inject your teammates' actual repositories here once they exist:
    // private final ReadingRepository readingRepository;
    // private final BaselineRepository baselineRepository;

    public void runDetection(String region, String signalType) {
        Optional<BaselineSnapshot> baseline = loadBaseline(region, signalType);

        if (baseline.isEmpty()) {
            log.info("No baseline for {}/{} — skipping detection", region, signalType);
            return;
        }

        List<ReadingSnapshot> recent = loadRecentReadings(region, signalType, config.consecutiveReadings());

        Optional<Deviation> deviation = DeviationDetector.detect(
                baseline, recent, config.zThreshold(), config.consecutiveReadings());

        if (deviation.isEmpty()) {
            return;
        }

        var openStatuses = List.of(EventStatus.NEW, EventStatus.UNDER_REVIEW);
        boolean alreadyOpen = eventRepository
                .findFirstByRegionAndSignalTypeAndStatusIn(region, signalType, openStatuses)
                .isPresent();
        if (alreadyOpen) {
            log.info("Event already open for {}/{} — not creating a duplicate", region, signalType);
            return;
        }

        DetectedEvent event = new DetectedEvent();
        event.setRegion(deviation.get().region());
        event.setSignalType(deviation.get().signalType());
        event.setDetectedAt(deviation.get().timestamp());
        event.setDeviation(deviation.get().deviationSize());
        event.setStatus(EventStatus.NEW);
        eventRepository.save(event);
        log.info("Created NEW event for {}/{} — deviation={}", region, signalType, deviation.get().deviationSize());
    }

    private Optional<BaselineSnapshot> loadBaseline(String region, String signalType) {
        // TODO: call baselineRepository, map the real Baseline entity into a BaselineSnapshot
        throw new UnsupportedOperationException("wire this to S1-5's baseline repo");
    }

    private List<ReadingSnapshot> loadRecentReadings(String region, String signalType, int limit) {
        // TODO: call readingRepository, map real Reading entities into ReadingSnapshots,
        // ordered oldest -> newest, excluding malformed readings
        throw new UnsupportedOperationException("wire this to S1-4's reading repo");
    }
}