package com.company.cms.audit;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.company.cms.auth.AuthUser;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private final CopyOnWriteArrayList<AuditLogEntry> entries = new CopyOnWriteArrayList<>();

    public void record(AuthUser actor, String action, String targetType, String targetId, String summary) {
        entries.add(new AuditLogEntry(UUID.randomUUID(), actor, action, targetType, targetId, summary, Instant.now()));
    }

    public void recordSystem(String action, String targetType, String targetId, String summary) {
        entries.add(new AuditLogEntry(UUID.randomUUID(), null, action, targetType, targetId, summary, Instant.now()));
    }

    public List<AuditLogEntry> list() {
        return entries.stream()
                .sorted(Comparator.comparing(AuditLogEntry::createdAt).reversed())
                .toList();
    }

    public record AuditLogEntry(
            UUID id,
            AuthUser actor,
            String action,
            String targetType,
            String targetId,
            String summary,
            Instant createdAt
    ) {
    }
}
