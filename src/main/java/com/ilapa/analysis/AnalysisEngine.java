package com.ilapa.analysis;

import com.ilapa.model.MetricSample;
import com.ilapa.model.PerformanceEvent;

import java.util.ArrayList;
import java.util.List;

public class AnalysisEngine {

    private final SpikeDetector spikeDetector = new SpikeDetector();
    private final MemoryGrowthDetector memoryGrowthDetector = new MemoryGrowthDetector();
    private final TrendAnalyzer trendAnalyzer = new TrendAnalyzer();
    private final PerformanceScorer scorer = new PerformanceScorer();

    private final List<PerformanceEvent> events = new ArrayList<>();

    public List<PerformanceEvent> processSample(MetricSample sample) {
        List<PerformanceEvent> newEvents = new ArrayList<>();

        scorer.addSample(sample);
        trendAnalyzer.addAndGetSmoothedCpu(sample);
        trendAnalyzer.addAndGetSmoothedMemory(sample);

        PerformanceEvent spikeEvent = spikeDetector.check(sample);
        if (spikeEvent != null) {
            newEvents.add(spikeEvent);
        }

        PerformanceEvent growthEvent = memoryGrowthDetector.check(sample);
        if (growthEvent != null) {
            newEvents.add(growthEvent);
        }

        for (PerformanceEvent e : newEvents) {
            scorer.registerEvent();
            events.add(e);
        }

        return newEvents;
    }

    public int getCurrentScore() {
        return scorer.calculateScore();
    }

    public boolean isDegrading() {
        return trendAnalyzer.isPerformanceDegrading();
    }

    public List<PerformanceEvent> getAllEvents() {
        return events;
    }
}
