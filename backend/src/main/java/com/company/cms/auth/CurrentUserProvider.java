package com.company.cms.auth;

import com.company.cms.common.api.ApiException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {
    public AuthenticatedUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw ApiException.unauthorized("AUTH_REQUIRED", "Authentication is required.");
        }
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return fromJwt(jwt);
        }
        throw ApiException.unauthorized("AUTH_REQUIRED", "Unsupported authentication principal.");
    }

    private AuthenticatedUser fromJwt(Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaimAsString("user_id"));
        UUID departmentId = UUID.fromString(jwt.getClaimAsString("department_id"));
        Set<RoleCode> roles = new LinkedHashSet<>();
        List<String> roleClaims = jwt.getClaimAsStringList("roles");
        if (roleClaims != null) {
            for (String role : roleClaims) {
                roles.add(RoleCode.valueOf(role));
            }
        }
        return new AuthenticatedUser(
            userId,
            jwt.getClaimAsString("email"),
            jwt.getClaimAsString("name"),
            departmentId,
            roles
        );
    }
}
