package com.cs203.healthwatch.detection;

import java.util.List;
import java.util.Optional;

public class DeviationDetector {

    /**
     * @param baseline        may be empty — caller must handle "no baseline" case
     * @param recentReadings  most recent readings for this region+signal, ordered oldest→newest
     * @return a Deviation if the sustained-spike condition is met, otherwise empty
     */
    public static Optional<Deviation> detect(
            Optional<BaselineSnapshot> baseline,
            List<ReadingSnapshot> recentReadings,
            double zThreshold,
            int consecutiveRequired
    ) {
        if (baseline.isEmpty()) {
            return Optional.empty(); // "skip detection, log a message" happens in the caller
        }
        if (recentReadings.size() < consecutiveRequired) {
            return Optional.empty(); // not enough data yet
        }

        BaselineSnapshot b = baseline.get();

        List<ReadingSnapshot> lastN = recentReadings.subList(
                recentReadings.size() - consecutiveRequired, recentReadings.size());

        boolean allBreached = true;
        double maxAbsZ = 0.0;

        for (ReadingSnapshot r : lastN) {
            if (r == null) {
                return Optional.empty(); // gap = no event
            }
            double z = robustZ(r.value(), b.median(), b.scaledMad());
            maxAbsZ = Math.max(maxAbsZ, Math.abs(z));
            if (z <= zThreshold) {
                allBreached = false;
            }
        }

        if (!allBreached) {
            return Optional.empty(); // single spike among the N = no event
        }

        ReadingSnapshot latest = lastN.get(lastN.size() - 1);
        return Optional.of(new Deviation(
                latest.region(),
                latest.signalType(),
                latest.observedAt(),
                maxAbsZ
        ));
    }

    private static double robustZ(double value, double median, double scaledMad) {
        if (scaledMad == 0) {
            return value == median ? 0.0 : Double.MAX_VALUE;
        }
        return (value - median) / scaledMad;
    }
}