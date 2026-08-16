package com.ilapa.monitor;

import oshi.software.os.OSProcess;

public class CpuMonitor {

    // need a previous snapshot to compare ticks against, first read is always 0
    private OSProcess previousSnapshot;

    public double getCpuUsage(OSProcess current) {
        if (previousSnapshot == null) {
            previousSnapshot = current;
            return 0.0;
        }

        double load = current.getProcessCpuLoadBetweenTicks(previousSnapshot) * 100;
        previousSnapshot = current;

        // oshi can return small negative numbers on the odd read, just clamp it
        if (load < 0) {
            load = 0;
        }
        return load;
    }
}
