package com.ilapa.analysis;

import com.ilapa.model.MetricSample;
import com.ilapa.model.PerformanceEvent;

import java.time.Duration;
import java.time.LocalDateTime;

public class SpikeDetector {

    private static final double CPU_THRESHOLD = 80.0;

    private boolean inSpike = false;
    private LocalDateTime spikeStart;
    private double peakDuringSpike = 0;

    public PerformanceEvent check(MetricSample sample) {
        if (sample.getCpuUsage() >= CPU_THRESHOLD) {
            if (!inSpike) {
                inSpike = true;
                spikeStart = sample.getTimestamp();
                peakDuringSpike = sample.getCpuUsage();
            } else {
                peakDuringSpike = Math.max(peakDuringSpike, sample.getCpuUsage());
            }
            return null;
        }

        // cpu dropped back down, close out the spike and report it
        if (inSpike) {
            inSpike = false;
            long durationSeconds = Duration.between(spikeStart, sample.getTimestamp()).getSeconds();

            String desc = String.format("CPU spike detected, peak %.1f%%, lasted %d sec",
                    peakDuringSpike, durationSeconds);

            return new PerformanceEvent(sample.getSessionId(), "CPU_SPIKE", desc);
        }

        return null;
    }
}
