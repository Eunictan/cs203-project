package com.cs203.healthwatch.detection;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

class DeviationDetectorTest {

    BaselineSnapshot baseline = new BaselineSnapshot("north", "AQI", 50.0, 10.0);

    ReadingSnapshot reading(double value) {
        return new ReadingSnapshot("north", "AQI", value, Instant.now());
    }

    @Test
    void belowThreshold_noEvent() {
        var readings = List.of(reading(55), reading(58), reading(60));
        assertThat(DeviationDetector.detect(Optional.of(baseline), readings, 3.0, 3)).isEmpty();
    }

    @Test
    void singleSpike_noEvent() {
        var readings = List.of(reading(55), reading(58), reading(120));
        assertThat(DeviationDetector.detect(Optional.of(baseline), readings, 3.0, 3)).isEmpty();
    }

    @Test
    void sustainedSpike_event() {
        var readings = List.of(reading(120), reading(125), reading(130));
        var result = DeviationDetector.detect(Optional.of(baseline), readings, 3.0, 3);
        assertThat(result).isPresent();
        assertThat(result.get().region()).isEqualTo("north");
    }

    @Test
    void gapInReadings_noEvent() {
        var readings = new java.util.ArrayList<ReadingSnapshot>();
        readings.add(reading(120));
        readings.add(null);
        readings.add(reading(130));
        assertThat(DeviationDetector.detect(Optional.of(baseline), readings, 3.0, 3)).isEmpty();
    }

    @Test
    void missingBaseline_noEvent() {
        var readings = List.of(reading(120), reading(125), reading(130));
        assertThat(DeviationDetector.detect(Optional.empty(), readings, 3.0, 3)).isEmpty();
    }
}