package com.ilapa.database;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_FOLDER = "database";
    private static final String DB_FILE = DB_FOLDER + "/performance.db";
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Files.createDirectories(Path.of(DB_FOLDER));
                connection = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
                initSchema();
            } catch (SQLException | java.io.IOException e) {
                throw new RuntimeException("Could not open database", e);
            }
        }
        return connection;
    }

    private static void initSchema() throws SQLException {
        String sessions = """
                CREATE TABLE IF NOT EXISTS sessions (
                    session_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    application_name TEXT NOT NULL,
                    start_time TEXT NOT NULL,
                    end_time TEXT,
                    duration INTEGER
                )
                """;

        String metrics = """
                CREATE TABLE IF NOT EXISTS metrics (
                    metric_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    session_id INTEGER NOT NULL,
                    timestamp TEXT NOT NULL,
                    cpu_usage REAL,
                    memory_usage INTEGER,
                    disk_read INTEGER,
                    disk_write INTEGER,
                    network_received INTEGER,
                    network_sent INTEGER,
                    thread_count INTEGER,
                    FOREIGN KEY(session_id) REFERENCES sessions(session_id)
                )
                """;

        String events = """
                CREATE TABLE IF NOT EXISTS events (
                    event_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    session_id INTEGER NOT NULL,
                    timestamp TEXT NOT NULL,
                    event_type TEXT,
                    description TEXT,
                    FOREIGN KEY(session_id) REFERENCES sessions(session_id)
                )
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sessions);
            stmt.execute(metrics);
            stmt.execute(events);
        }
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing db: " + e.getMessage());
        }
    }
}
