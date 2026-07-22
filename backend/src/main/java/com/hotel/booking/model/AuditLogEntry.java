package com.hotel.booking.model;

import java.time.LocalDateTime;

public class AuditLogEntry {
    private Long id;
    private LocalDateTime timestamp;
    private String action;
    private String message;
    private String type;

    public AuditLogEntry(Long id, LocalDateTime timestamp, String action, String message, String type) {
        this.id = id;
        this.timestamp = timestamp;
        this.action = action;
        this.message = message;
        this.type = type;
    }

    public Long getId() { return id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getAction() { return action; }
    public String getMessage() { return message; }
    public String getType() { return type; }
}
