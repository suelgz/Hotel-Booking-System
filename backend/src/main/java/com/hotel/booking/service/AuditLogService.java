package com.hotel.booking.service;

import com.hotel.booking.model.AuditLogEntry;
import com.hotel.booking.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public AuditLogEntry record(String action, String message, String type) {
        AuditLogEntry entry = new AuditLogEntry(
                null,
                LocalDateTime.now(),
                action,
                message,
                type
        );
        return auditLogRepository.save(entry);
    }

    @Transactional(readOnly = true)
    public List<AuditLogEntry> getAll() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }

    @Transactional(readOnly = true)
    public List<AuditLogEntry> getRecent(int limit) {
        return getAll().stream().limit(limit).toList();
    }
}
