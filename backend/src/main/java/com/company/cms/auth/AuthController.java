package com.company.cms.auth;

import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @GetMapping("/me")
    public UserProfile me(@AuthenticationPrincipal AuthUser user) {
        return UserProfile.from(user);
    }

    public record UserProfile(
            UUID id,
            String email,
            String displayName,
            String department,
            Set<RoleCode> roles,
            Set<String> permissions
    ) {
        static UserProfile from(AuthUser user) {
            return new UserProfile(user.id(), user.email(), user.displayName(), user.department(), user.roles(), user.permissions());
        }
    }
}
