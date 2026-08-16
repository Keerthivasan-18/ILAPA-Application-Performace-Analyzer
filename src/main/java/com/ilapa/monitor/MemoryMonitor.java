package com.ilapa.monitor;

import oshi.software.os.OSProcess;

public class MemoryMonitor {

    public long getMemoryUsageMb(OSProcess process) {
        long bytes = process.getResidentSetSize();
        return bytes / (1024 * 1024);
    }
}
