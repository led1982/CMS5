package com.company.cms.admin.api;

import java.util.UUID;

import jakarta.validation.Valid;

import com.company.cms.admin.UserRoleAdminService;
import com.company.cms.auth.AuthUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class RoleAdminController {
    private final UserRoleAdminService userRoleAdminService;

    public RoleAdminController(UserRoleAdminService userRoleAdminService) {
        this.userRoleAdminService = userRoleAdminService;
    }

    @PutMapping("/{userId}/roles")
    AuthUser updateRoles(@PathVariable UUID userId, @Valid @RequestBody UserRoleAdminService.RoleAssignmentRequest request, @AuthenticationPrincipal AuthUser actor) {
        return userRoleAdminService.updateRoles(userId, request, actor);
    }
}
