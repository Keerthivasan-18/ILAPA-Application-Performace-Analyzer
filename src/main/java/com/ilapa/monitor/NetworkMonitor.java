package com.ilapa.monitor;

import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.util.List;

public class NetworkMonitor {

    // oshi doesnt expose per-process network stats on most platforms, so this
    // is tracking the whole machine's network throughput instead. not ideal
    // but its the best we can do without writing native code for this.
    private long lastRecv = -1;
    private long lastSent = -1;

    public long[] getNetworkDelta(HardwareAbstractionLayer hal) {
        List<NetworkIF> nets = hal.getNetworkIFs();

        long totalRecv = 0;
        long totalSent = 0;

        for (NetworkIF net : nets) {
            net.updateAttributes();
            totalRecv += net.getBytesRecv();
            totalSent += net.getBytesSent();
        }

        long recvDelta = 0;
        long sentDelta = 0;

        if (lastRecv != -1) {
            recvDelta = totalRecv - lastRecv;
            sentDelta = totalSent - lastSent;
        }

        lastRecv = totalRecv;
        lastSent = totalSent;

        if (recvDelta < 0) recvDelta = 0;
        if (sentDelta < 0) sentDelta = 0;

        return new long[]{recvDelta, sentDelta};
    }
}
