package com.ilapa.model;

import java.time.LocalDateTime;

public class PerformanceEvent {

    private long eventId;
    private long sessionId;
    private LocalDateTime timestamp;
    private String eventType;
    private String description;

    public PerformanceEvent() {
    }

    public PerformanceEvent(long sessionId, String eventType, String description) {
        this.sessionId = sessionId;
        this.timestamp = LocalDateTime.now();
        this.eventType = eventType;
        this.description = description;
    }

    public long getEventId() { return eventId; }
    public void setEventId(long eventId) { this.eventId = eventId; }

    public long getSessionId() { return sessionId; }
    public void setSessionId(long sessionId) { this.sessionId = sessionId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
