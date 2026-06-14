package com.company.cms.admin;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.company.cms.audit.AuditService;
import com.company.cms.auth.AuthUser;
import com.company.cms.auth.RoleCode;
import org.springframework.stereotype.Service;

@Service
public class UserRoleAdminService {
    private final AuditService auditService;
    private final ConcurrentMap<UUID, Set<RoleCode>> assignedRoles = new ConcurrentHashMap<>();

    public UserRoleAdminService(AuditService auditService) {
        this.auditService = auditService;
    }

    public AuthUser updateRoles(UUID userId, RoleAssignmentRequest request, AuthUser actor) {
        Set<RoleCode> roles = new LinkedHashSet<>(request.roles());
        assignedRoles.put(userId, roles);
        auditService.record(actor, "ROLE_ASSIGNED", "User", userId.toString(), "Roles changed to " + roles);
        return new AuthUser(userId, userId + "@example.com", "역할 변경 사용자", "Unknown", roles);
    }

    public record RoleAssignmentRequest(Set<RoleCode> roles) {
        public RoleAssignmentRequest {
            roles = roles == null || roles.isEmpty() ? new LinkedHashSet<>(Arrays.asList(RoleCode.VIEWER)) : roles;
        }
    }
}
