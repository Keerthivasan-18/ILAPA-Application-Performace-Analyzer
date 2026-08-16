package com.ilapa.database;

import com.ilapa.model.Session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SessionDao {

    public long insertSession(Session session) {
        String sql = "INSERT INTO sessions (application_name, start_time) VALUES (?, ?)";

        try (PreparedStatement ps = DatabaseManager.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, session.getApplicationName());
            ps.setString(2, session.getStartTime().toString());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to insert session: " + e.getMessage());
        }
        return -1;
    }

    public void closeSession(long sessionId, LocalDateTime endTime, long durationSeconds) {
        String sql = "UPDATE sessions SET end_time = ?, duration = ? WHERE session_id = ?";

        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, endTime.toString());
            ps.setLong(2, durationSeconds);
            ps.setLong(3, sessionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to close session: " + e.getMessage());
        }
    }

    public List<Session> getAllSessions() {
        List<Session> result = new ArrayList<>();
        String sql = "SELECT * FROM sessions ORDER BY start_time DESC";

        try (Statement stmt = DatabaseManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Failed to load sessions: " + e.getMessage());
        }
        return result;
    }

    public Session getSessionById(long sessionId) {
        String sql = "SELECT * FROM sessions WHERE session_id = ?";

        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to load session: " + e.getMessage());
        }
        return null;
    }

    private Session mapRow(ResultSet rs) throws SQLException {
        Session s = new Session();
        s.setSessionId(rs.getLong("session_id"));
        s.setApplicationName(rs.getString("application_name"));
        s.setStartTime(LocalDateTime.parse(rs.getString("start_time")));

        String end = rs.getString("end_time");
        if (end != null) {
            s.setEndTime(LocalDateTime.parse(end));
        }
        s.setDurationSeconds(rs.getLong("duration"));
        return s;
    }
}
