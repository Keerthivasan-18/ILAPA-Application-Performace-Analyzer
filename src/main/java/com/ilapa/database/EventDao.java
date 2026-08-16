package com.ilapa.database;

import com.ilapa.model.PerformanceEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventDao {

    public void insertEvent(PerformanceEvent event) {
        String sql = "INSERT INTO events (session_id, timestamp, event_type, description) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setLong(1, event.getSessionId());
            ps.setString(2, event.getTimestamp().toString());
            ps.setString(3, event.getEventType());
            ps.setString(4, event.getDescription());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to insert event: " + e.getMessage());
        }
    }

    public List<PerformanceEvent> getEventsForSession(long sessionId) {
        List<PerformanceEvent> result = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE session_id = ? ORDER BY timestamp ASC";

        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PerformanceEvent e = new PerformanceEvent();
                    e.setEventId(rs.getLong("event_id"));
                    e.setSessionId(rs.getLong("session_id"));
                    e.setTimestamp(LocalDateTime.parse(rs.getString("timestamp")));
                    e.setEventType(rs.getString("event_type"));
                    e.setDescription(rs.getString("description"));
                    result.add(e);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to load events: " + e.getMessage());
        }
        return result;
    }
}
