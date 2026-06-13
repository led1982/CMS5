package com.company.cms.admin.api;

import com.company.cms.audit.AuditAction;
import com.company.cms.auth.RoleCode;
import com.company.cms.content.domain.ContentStatus;
import com.company.cms.content.domain.ContentType;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class AdminDtos {
    private AdminDtos() {
    }

    public record ReplaceRolesRequest(Set<RoleCode> roles) {
    }

    public record AuditLogDto(
        UUID id,
        UUID actorId,
        String actorEmail,
        AuditAction action,
        String targetType,
        UUID targetId,
        String afterSnapshot,
        Instant createdAt
    ) {
    }

    public record ContentMetrics(
        Map<ContentStatus, Long> byStatus,
        Map<ContentType, Long> byType,
        long unacknowledgedAnnouncements,
        List<TopContent> topViewed
    ) {
    }

    public record TopContent(UUID contentId, String title, int viewCount) {
    }
}
