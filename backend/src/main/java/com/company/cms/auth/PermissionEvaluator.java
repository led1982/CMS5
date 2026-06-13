package com.company.cms.auth;

import org.springframework.stereotype.Component;

@Component
public class PermissionEvaluator {
    public boolean hasRole(AuthUser user, RoleCode role) {
        return user.roles().contains(role);
    }

    public boolean canManageContent(AuthUser user) {
        return user.roles().contains(RoleCode.ADMIN) || user.roles().contains(RoleCode.EDITOR) || user.roles().contains(RoleCode.REVIEWER);
    }

    public boolean canApproveContent(AuthUser user) {
        return user.roles().contains(RoleCode.ADMIN) || user.roles().contains(RoleCode.REVIEWER);
    }

    public boolean canAdminister(AuthUser user) {
        return user.roles().contains(RoleCode.ADMIN);
    }
}
