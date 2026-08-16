package com.ilapa.database;

import com.ilapa.model.MetricSample;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MetricDao {

    public void insertMetric(MetricSample sample) {
        String sql = """
                INSERT INTO metrics
                (session_id, timestamp, cpu_usage, memory_usage, disk_read, disk_write,
                 network_received, network_sent, thread_count)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setLong(1, sample.getSessionId());
            ps.setString(2, sample.getTimestamp().toString());
            ps.setDouble(3, sample.getCpuUsage());
            ps.setLong(4, sample.getMemoryUsageMb());
            ps.setLong(5, sample.getDiskRead());
            ps.setLong(6, sample.getDiskWrite());
            ps.setLong(7, sample.getNetworkReceived());
            ps.setLong(8, sample.getNetworkSent());
            ps.setInt(9, sample.getThreadCount());
            ps.executeUpdate();
        } catch (SQLException e) {
            // losing one row of metrics isnt worth crashing the whole session over
            System.err.println("Failed to insert metric: " + e.getMessage());
        }
    }

    public List<MetricSample> getMetricsForSession(long sessionId) {
        List<MetricSample> result = new ArrayList<>();
        String sql = "SELECT * FROM metrics WHERE session_id = ? ORDER BY timestamp ASC";

        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MetricSample m = new MetricSample();
                    m.setSessionId(rs.getLong("session_id"));
                    m.setTimestamp(LocalDateTime.parse(rs.getString("timestamp")));
                    m.setCpuUsage(rs.getDouble("cpu_usage"));
                    m.setMemoryUsageMb(rs.getLong("memory_usage"));
                    m.setDiskRead(rs.getLong("disk_read"));
                    m.setDiskWrite(rs.getLong("disk_write"));
                    m.setNetworkReceived(rs.getLong("network_received"));
                    m.setNetworkSent(rs.getLong("network_sent"));
                    m.setThreadCount(rs.getInt("thread_count"));
                    result.add(m);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to load metrics: " + e.getMessage());
        }
        return result;
    }
}
