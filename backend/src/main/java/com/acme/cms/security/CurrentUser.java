package com.acme.cms.security;

import com.acme.cms.api.ApiException;
import com.acme.cms.security.model.UserAccount;
import com.acme.cms.security.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CurrentUser {
    private final UserRepository users;

    public CurrentUser(UserRepository users) {
        this.users = users;
    }

    @Transactional(readOnly = true)
    public UserAccount get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", "Authentication is required.");
        }
        return users.findByEmailIgnoreCase(authentication.getName())
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", "Unknown user."));
    }

    public void requirePermission(String permission) {
        UserAccount user = get();
        if (!hasPermission(user, permission)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Permission required: " + permission);
        }
    }

    public boolean hasPermission(UserAccount user, String permission) {
        return user.hasPermission(permission) || user.hasPermission("ADMIN_ACCESS");
    }
}
