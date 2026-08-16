package com.ilapa.monitor;

import oshi.software.os.OSProcess;

public class DiskMonitor {

    private long lastReadBytes = -1;
    private long lastWriteBytes = -1;

    public long[] getDiskDelta(OSProcess process) {
        long readBytes = process.getBytesRead();
        long writeBytes = process.getBytesWritten();

        long readDelta = 0;
        long writeDelta = 0;

        if (lastReadBytes != -1) {
            readDelta = readBytes - lastReadBytes;
            writeDelta = writeBytes - lastWriteBytes;
        }

        lastReadBytes = readBytes;
        lastWriteBytes = writeBytes;

        // counters shouldnt go backwards but just being safe here
        if (readDelta < 0) readDelta = 0;
        if (writeDelta < 0) writeDelta = 0;

        return new long[]{readDelta, writeDelta};
    }
}
