package com.company.cms.auth;

import java.util.Set;

public enum RoleCode {
    ADMIN(Set.of("content:create", "content:update", "content:approve", "content:publish", "admin:manage", "analytics:read")),
    EDITOR(Set.of("content:create", "content:update", "content:submit")),
    REVIEWER(Set.of("content:approve", "content:read")),
    EMPLOYEE(Set.of("portal:read", "portal:bookmark", "notice:acknowledge")),
    VIEWER(Set.of("portal:read"));

    private final Set<String> permissions;

    RoleCode(Set<String> permissions) {
        this.permissions = permissions;
    }

    public Set<String> permissions() {
        return permissions;
    }
}
