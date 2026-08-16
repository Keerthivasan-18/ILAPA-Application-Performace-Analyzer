package com.ilapa.analysis;

import com.ilapa.model.MetricSample;
import com.ilapa.model.PerformanceEvent;

public class MemoryGrowthDetector {

    private static final long GROWTH_THRESHOLD_MB = 500;

    private long startMemory = -1;
    private boolean alreadyFlagged = false;

    public PerformanceEvent check(MetricSample sample) {
        if (startMemory == -1) {
            startMemory = sample.getMemoryUsageMb();
            return null;
        }

        long growth = sample.getMemoryUsageMb() - startMemory;

        // only flag this once per session, otherwise it just keeps firing every tick
        if (growth >= GROWTH_THRESHOLD_MB && !alreadyFlagged) {
            alreadyFlagged = true;
            String desc = String.format("Memory grew by %d MB since session start (now %d MB)",
                    growth, sample.getMemoryUsageMb());
            return new PerformanceEvent(sample.getSessionId(), "MEMORY_GROWTH", desc);
        }

        return null;
    }

    public long getStartMemory() {
        return startMemory;
    }
}
