package com.ilapa.monitor;

import com.ilapa.model.MetricSample;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MonitoringEngine {

    private final SystemInfo systemInfo = new SystemInfo();
    private final ProcessMonitor processMonitor = new ProcessMonitor(systemInfo.getOperatingSystem());
    private final CpuMonitor cpuMonitor = new CpuMonitor();
    private final MemoryMonitor memoryMonitor = new MemoryMonitor();
    private final DiskMonitor diskMonitor = new DiskMonitor();
    private final NetworkMonitor networkMonitor = new NetworkMonitor();
    private final ThreadMonitor threadMonitor = new ThreadMonitor();

    private ScheduledExecutorService scheduler;
    private long currentSessionId;
    private Consumer<MetricSample> onSample;
    private volatile boolean running = false;

    public boolean attachToApplication(String applicationName) {
        return processMonitor.lockOntoProcess(applicationName);
    }

    public List<OSProcess> getRunningProcesses() {
        return processMonitor.listRunningProcesses();
    }

    public void start(long sessionId, Consumer<MetricSample> onSample) {
        this.currentSessionId = sessionId;
        this.onSample = onSample;
        this.running = true;

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::pollOnce, 0, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        running = false;
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    public boolean isRunning() {
        return running;
    }

    private void pollOnce() {
        try {
            OSProcess process = processMonitor.refresh();
            if (process == null) {
                // app was probably closed, no point spamming errors after this
                stop();
                return;
            }

            double cpu = cpuMonitor.getCpuUsage(process);
            long memoryMb = memoryMonitor.getMemoryUsageMb(process);
            long[] diskDelta = diskMonitor.getDiskDelta(process);
            long[] netDelta = networkMonitor.getNetworkDelta(systemInfo.getHardware());
            int threads = threadMonitor.getThreadCount(process);

            MetricSample sample = new MetricSample(
                    currentSessionId, cpu, memoryMb, diskDelta[0], diskDelta[1],
                    netDelta[0], netDelta[1], threads
            );

            if (onSample != null) {
                onSample.accept(sample);
            }
        } catch (Exception e) {
            // one bad read shouldnt kill the whole monitoring session
            System.err.println("Error while polling metrics: " + e.getMessage());
        }
    }
}
