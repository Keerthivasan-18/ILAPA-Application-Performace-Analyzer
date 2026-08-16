package com.ilapa.model;

import java.time.LocalDateTime;

public class MetricSample {

    private long sessionId;
    private LocalDateTime timestamp;
    private double cpuUsage;
    private long memoryUsageMb;
    private long diskRead;
    private long diskWrite;
    private long networkReceived;
    private long networkSent;
    private int threadCount;

    public MetricSample() {
    }

    public MetricSample(long sessionId, double cpuUsage, long memoryUsageMb, long diskRead, long diskWrite,
                         long networkReceived, long networkSent, int threadCount) {
        this.sessionId = sessionId;
        this.timestamp = LocalDateTime.now();
        this.cpuUsage = cpuUsage;
        this.memoryUsageMb = memoryUsageMb;
        this.diskRead = diskRead;
        this.diskWrite = diskWrite;
        this.networkReceived = networkReceived;
        this.networkSent = networkSent;
        this.threadCount = threadCount;
    }

    public long getSessionId() { return sessionId; }
    public void setSessionId(long sessionId) { this.sessionId = sessionId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }

    public long getMemoryUsageMb() { return memoryUsageMb; }
    public void setMemoryUsageMb(long memoryUsageMb) { this.memoryUsageMb = memoryUsageMb; }

    public long getDiskRead() { return diskRead; }
    public void setDiskRead(long diskRead) { this.diskRead = diskRead; }

    public long getDiskWrite() { return diskWrite; }
    public void setDiskWrite(long diskWrite) { this.diskWrite = diskWrite; }

    public long getNetworkReceived() { return networkReceived; }
    public void setNetworkReceived(long networkReceived) { this.networkReceived = networkReceived; }

    public long getNetworkSent() { return networkSent; }
    public void setNetworkSent(long networkSent) { this.networkSent = networkSent; }

    public int getThreadCount() { return threadCount; }
    public void setThreadCount(int threadCount) { this.threadCount = threadCount; }
}
