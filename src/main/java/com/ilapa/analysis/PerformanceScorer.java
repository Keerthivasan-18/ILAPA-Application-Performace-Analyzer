package com.ilapa.analysis;

import com.ilapa.model.MetricSample;

import java.util.ArrayList;
import java.util.List;

public class PerformanceScorer {

    private final List<Double> cpuSamples = new ArrayList<>();
    private final List<Long> memorySamples = new ArrayList<>();
    private int eventCount = 0;

    public void addSample(MetricSample sample) {
        cpuSamples.add(sample.getCpuUsage());
        memorySamples.add(sample.getMemoryUsageMb());
    }

    public void registerEvent() {
        eventCount++;
    }

    public int calculateScore() {
        if (cpuSamples.isEmpty()) {
            return 100;
        }

        double cpuStability = scoreStability(cpuSamples);
        double memoryStability = scoreStability(toDoubleList(memorySamples));
        double efficiency = scoreEfficiency();
        double eventPenalty = Math.min(eventCount * 5, 40);

        double raw = (cpuStability * 0.35) + (memoryStability * 0.35) + (efficiency * 0.30) - eventPenalty;

        int score = (int) Math.round(raw);
        // clamp it, negative or 100+ scores dont make sense to show the user
        if (score < 0) score = 0;
        if (score > 100) score = 100;

        return score;
    }

    private double scoreStability(List<Double> values) {
        double mean = average(values);
        if (mean == 0) return 100;

        double variance = 0;
        for (double v : values) {
            variance += Math.pow(v - mean, 2);
        }
        variance /= values.size();
        double stdDev = Math.sqrt(variance);

        double coefficientOfVariation = stdDev / mean;

        // lower variation should score higher, so we flip it around
        double stability = 100 - (coefficientOfVariation * 100);
        return Math.max(0, Math.min(100, stability));
    }

    private double scoreEfficiency() {
        double avgCpu = average(cpuSamples);
        double efficiency = 100 - avgCpu;
        return Math.max(0, Math.min(100, efficiency));
    }

    private double average(List<Double> values) {
        double sum = 0;
        for (double v : values) {
            sum += v;
        }
        return values.isEmpty() ? 0 : sum / values.size();
    }

    private List<Double> toDoubleList(List<Long> values) {
        List<Double> result = new ArrayList<>();
        for (Long v : values) {
            result.add(v.doubleValue());
        }
        return result;
    }
}
