package com.ilapa.model;

import java.time.LocalDateTime;

public class Session {

    private long sessionId;
    private String applicationName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long durationSeconds;

    public Session() {
    }

    public Session(String applicationName) {
        this.applicationName = applicationName;
        this.startTime = LocalDateTime.now();
    }

    public long getSessionId() { return sessionId; }
    public void setSessionId(long sessionId) { this.sessionId = sessionId; }

    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public long getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(long durationSeconds) { this.durationSeconds = durationSeconds; }
}
