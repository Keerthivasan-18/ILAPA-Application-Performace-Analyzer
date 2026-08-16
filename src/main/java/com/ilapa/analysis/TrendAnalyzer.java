package com.ilapa.analysis;

import com.ilapa.model.MetricSample;

import java.util.ArrayList;
import java.util.List;

public class TrendAnalyzer {

    private static final int WINDOW_SIZE = 5;

    private final List<Double> cpuHistory = new ArrayList<>();
    private final List<Long> memoryHistory = new ArrayList<>();

    public double addAndGetSmoothedCpu(MetricSample sample) {
        cpuHistory.add(sample.getCpuUsage());
        return movingAverage(cpuHistory);
    }

    public double addAndGetSmoothedMemory(MetricSample sample) {
        memoryHistory.add(sample.getMemoryUsageMb());
        List<Double> asDouble = new ArrayList<>();
        for (Long m : memoryHistory) {
            asDouble.add(m.doubleValue());
        }
        return movingAverage(asDouble);
    }

    private double movingAverage(List<Double> values) {
        int size = values.size();
        int start = Math.max(0, size - WINDOW_SIZE);
        double sum = 0;
        int count = 0;

        for (int i = start; i < size; i++) {
            sum += values.get(i);
            count++;
        }

        return count == 0 ? 0 : sum / count;
    }

    public boolean isMemoryTrendingUp() {
        if (memoryHistory.size() < 2) {
            return false;
        }
        long first = memoryHistory.get(0);
        long last = memoryHistory.get(memoryHistory.size() - 1);
        return last > first;
    }

    public boolean isPerformanceDegrading() {
        // rough heuristic, not scientific at all - if cpu is climbing a lot AND
        // memory is trending up too, thats probably not a great sign
        if (cpuHistory.size() < WINDOW_SIZE) {
            return false;
        }

        double earlyCpuAvg = averageOfFirst(cpuHistory, WINDOW_SIZE);
        double lateCpuAvg = averageOfLast(cpuHistory, WINDOW_SIZE);

        return lateCpuAvg > earlyCpuAvg * 1.3 && isMemoryTrendingUp();
    }

    private double averageOfFirst(List<Double> values, int n) {
        int limit = Math.min(n, values.size());
        double sum = 0;
        for (int i = 0; i < limit; i++) {
            sum += values.get(i);
        }
        return sum / limit;
    }

    private double averageOfLast(List<Double> values, int n) {
        int size = values.size();
        int start = Math.max(0, size - n);
        double sum = 0;
        int count = 0;
        for (int i = start; i < size; i++) {
            sum += values.get(i);
            count++;
        }
        return sum / count;
    }
}
