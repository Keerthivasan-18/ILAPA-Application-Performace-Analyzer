package com.ilapa.reports;

import com.ilapa.database.EventDao;
import com.ilapa.database.MetricDao;
import com.ilapa.database.SessionDao;
import com.ilapa.model.MetricSample;
import com.ilapa.model.PerformanceEvent;
import com.ilapa.model.Session;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportGenerator {

    private final SessionDao sessionDao = new SessionDao();
    private final MetricDao metricDao = new MetricDao();
    private final EventDao eventDao = new EventDao();

    public String generateSessionReport(long sessionId) {
        Session session = sessionDao.getSessionById(sessionId);
        if (session == null) {
            return "Session not found.";
        }

        List<MetricSample> metrics = metricDao.getMetricsForSession(sessionId);
        List<PerformanceEvent> events = eventDao.getEventsForSession(sessionId);

        double avgCpu = 0, peakCpu = 0;
        long avgMem = 0, peakMem = 0;

        if (!metrics.isEmpty()) {
            double cpuSum = 0;
            long memSum = 0;

            for (MetricSample m : metrics) {
                cpuSum += m.getCpuUsage();
                memSum += m.getMemoryUsageMb();
                peakCpu = Math.max(peakCpu, m.getCpuUsage());
                peakMem = Math.max(peakMem, m.getMemoryUsageMb());
            }

            avgCpu = cpuSum / metrics.size();
            avgMem = memSum / metrics.size();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== ILAPA Session Report ===\n");
        sb.append("Application: ").append(session.getApplicationName()).append("\n");
        sb.append("Start Time: ").append(session.getStartTime()).append("\n");
        sb.append("End Time: ").append(session.getEndTime()).append("\n");
        sb.append("Duration: ").append(session.getDurationSeconds()).append(" sec\n\n");

        sb.append("-- Resource Usage --\n");
        sb.append(String.format("Average CPU: %.1f%%\n", avgCpu));
        sb.append(String.format("Peak CPU: %.1f%%\n", peakCpu));
        sb.append("Average Memory: ").append(avgMem).append(" MB\n");
        sb.append("Peak Memory: ").append(peakMem).append(" MB\n\n");

        sb.append("-- Detected Events (").append(events.size()).append(") --\n");
        if (events.isEmpty()) {
            sb.append("No abnormal events detected.\n");
        } else {
            for (PerformanceEvent e : events) {
                sb.append("[").append(e.getEventType()).append("] ").append(e.getDescription()).append("\n");
            }
        }

        return sb.toString();
    }

    public void exportMetricsToCsv(long sessionId, String filePath) throws IOException {
        List<MetricSample> metrics = metricDao.getMetricsForSession(sessionId);

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("timestamp,cpu_usage,memory_mb,disk_read,disk_write,network_received,network_sent,threads\n");

            for (MetricSample m : metrics) {
                writer.write(String.format("%s,%.2f,%d,%d,%d,%d,%d,%d\n",
                        m.getTimestamp(), m.getCpuUsage(), m.getMemoryUsageMb(),
                        m.getDiskRead(), m.getDiskWrite(), m.getNetworkReceived(),
                        m.getNetworkSent(), m.getThreadCount()));
            }
        }
    }

    // compares two past sessions, handy for checking if a new build got slower
    public String compareSessions(long sessionIdA, long sessionIdB) {
        List<MetricSample> metricsA = metricDao.getMetricsForSession(sessionIdA);
        List<MetricSample> metricsB = metricDao.getMetricsForSession(sessionIdB);

        double avgCpuA = averageCpu(metricsA);
        double avgCpuB = averageCpu(metricsB);
        double avgMemA = averageMemory(metricsA);
        double avgMemB = averageMemory(metricsB);

        double cpuChange = avgCpuA == 0 ? 0 : ((avgCpuB - avgCpuA) / avgCpuA) * 100;
        double memChange = avgMemA == 0 ? 0 : ((avgMemB - avgMemA) / avgMemA) * 100;

        StringBuilder sb = new StringBuilder();
        sb.append("=== Session Comparison ===\n");
        sb.append(String.format("Session A - Avg CPU: %.1f%%, Avg Mem: %.0f MB\n", avgCpuA, avgMemA));
        sb.append(String.format("Session B - Avg CPU: %.1f%%, Avg Mem: %.0f MB\n", avgCpuB, avgMemB));
        sb.append(String.format("CPU Change: %+.1f%%\n", cpuChange));
        sb.append(String.format("Memory Change: %+.1f%%\n", memChange));

        return sb.toString();
    }

    private double averageCpu(List<MetricSample> metrics) {
        if (metrics.isEmpty()) return 0;
        double sum = 0;
        for (MetricSample m : metrics) sum += m.getCpuUsage();
        return sum / metrics.size();
    }

    private double averageMemory(List<MetricSample> metrics) {
        if (metrics.isEmpty()) return 0;
        double sum = 0;
        for (MetricSample m : metrics) sum += m.getMemoryUsageMb();
        return sum / metrics.size();
    }
}
