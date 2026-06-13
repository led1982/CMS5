package com.company.cms.admin.api;

import com.company.cms.admin.api.AdminDtos.AuditLogDto;
import com.company.cms.admin.api.AdminDtos.ContentMetrics;
import com.company.cms.admin.api.AdminDtos.ReplaceRolesRequest;
import com.company.cms.admin.service.ContentMetricsService;
import com.company.cms.admin.service.UserRoleAdminService;
import com.company.cms.audit.AuditAction;
import com.company.cms.audit.AuditLog;
import com.company.cms.audit.AuditLogQueryService;
import com.company.cms.auth.RoleCode;
import com.company.cms.content.api.ContentDtos.UserSummary;
import com.company.cms.content.api.ContentMapper;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final UserRoleAdminService userRoleAdminService;
    private final AuditLogQueryService auditLogQueryService;
    private final ContentMetricsService contentMetricsService;
    private final ContentMapper mapper;

    public AdminController(UserRoleAdminService userRoleAdminService, AuditLogQueryService auditLogQueryService,
            ContentMetricsService contentMetricsService, ContentMapper mapper) {
        this.userRoleAdminService = userRoleAdminService;
        this.auditLogQueryService = auditLogQueryService;
        this.contentMetricsService = contentMetricsService;
        this.mapper = mapper;
    }

    @GetMapping("/users")
    public List<UserSummary> users(@RequestParam(required = false) String q,
            @RequestParam(required = false) RoleCode role) {
        return userRoleAdminService.list(q, role).stream().map(mapper::user).toList();
    }

    @PatchMapping("/users/{userId}/roles")
    public UserSummary replaceRoles(@PathVariable UUID userId, @RequestBody ReplaceRolesRequest request) {
        return mapper.user(userRoleAdminService.replaceRoles(userId, request));
    }

    @GetMapping("/audit-logs")
    public List<AuditLogDto> auditLogs(
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) UUID targetId,
            @RequestParam(required = false) UUID actorId) {
        return auditLogQueryService.search(action, targetId, actorId).stream().map(this::auditLog).toList();
    }

    @GetMapping("/metrics/content")
    public ContentMetrics contentMetrics() {
        return contentMetricsService.metrics();
    }

    private AuditLogDto auditLog(AuditLog log) {
        return new AuditLogDto(
            log.getId(),
            log.getActor().getId(),
            log.getActor().getEmail(),
            log.getAction(),
            log.getTargetType(),
            log.getTargetId(),
            log.getAfterSnapshot(),
            log.getCreatedAt()
        );
    }
}
