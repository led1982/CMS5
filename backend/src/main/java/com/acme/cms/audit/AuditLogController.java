package com.acme.cms.audit;

import com.acme.cms.api.ApiDtos;
import com.acme.cms.api.CmsMapper;
import com.acme.cms.security.CurrentUser;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/audit-logs")
public class AuditLogController {
    private final AuditEventRepository auditEvents;
    private final CurrentUser currentUser;
    private final CmsMapper mapper;

    public AuditLogController(AuditEventRepository auditEvents, CurrentUser currentUser, CmsMapper mapper) {
        this.auditEvents = auditEvents;
        this.currentUser = currentUser;
        this.mapper = mapper;
    }

    @GetMapping
    ApiDtos.AuditLogPage list(
        @RequestParam(required = false) String targetType,
        @RequestParam(required = false) String actorUserId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        currentUser.requirePermission("AUDIT_READ");
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "occurredAt"));
        var result = targetType == null || targetType.isBlank()
            ? auditEvents.findAll(pageable)
            : auditEvents.findByTargetTypeIgnoreCase(targetType, pageable);
        var items = result.getContent().stream()
            .filter(event -> actorUserId == null || (event.getActor() != null && event.getActor().getId().toString().equals(actorUserId)))
            .map(mapper::toAuditLog)
            .toList();
        return new ApiDtos.AuditLogPage(items, page, size, result.getTotalElements());
    }
}
