package com.company.cms.auth;

import java.util.Set;
import java.util.UUID;

public record AuthenticatedUser(
    UUID id,
    String email,
    String displayName,
    UUID departmentId,
    Set<RoleCode> roles
) {
    public boolean hasAnyRole(RoleCode... requiredRoles) {
        for (RoleCode requiredRole : requiredRoles) {
            if (roles.contains(requiredRole)) {
                return true;
            }
        }
        return false;
    }
}
