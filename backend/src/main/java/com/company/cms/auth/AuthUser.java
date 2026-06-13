package com.company.cms.auth;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record AuthUser(
        UUID id,
        String email,
        String displayName,
        String department,
        Set<RoleCode> roles
) {
    public Set<String> permissions() {
        return roles.stream().flatMap(role -> role.permissions().stream()).collect(Collectors.toUnmodifiableSet());
    }
}
