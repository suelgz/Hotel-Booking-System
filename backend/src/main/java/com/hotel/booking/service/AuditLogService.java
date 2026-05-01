package com.hotel.booking.service;

import com.hotel.booking.model.AuditLogEntry;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AuditLogService {
    private final List<AuditLogEntry> entries = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public AuditLogEntry record(String action, String message, String type) {
        AuditLogEntry entry = new AuditLogEntry(
                idCounter.getAndIncrement(),
                LocalDateTime.now(),
                action,
                message,
                type
        );
        entries.add(entry);
        return entry;
    }

    public List<AuditLogEntry> getAll() {
        return entries.stream()
                .sorted(Comparator.comparing(AuditLogEntry::getTimestamp).reversed())
                .toList();
    }

    public List<AuditLogEntry> getRecent(int limit) {
        return getAll().stream().limit(limit).toList();
    }
}
