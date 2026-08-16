package com.ilapa.monitor;

import oshi.software.os.OSProcess;

public class ThreadMonitor {

    public int getThreadCount(OSProcess process) {
        return process.getThreadCount();
    }
}
