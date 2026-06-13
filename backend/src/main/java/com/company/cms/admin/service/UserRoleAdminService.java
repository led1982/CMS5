package com.company.cms.admin.service;

import com.company.cms.admin.api.AdminDtos.ReplaceRolesRequest;
import com.company.cms.audit.AuditAction;
import com.company.cms.audit.AuditLoggingService;
import com.company.cms.auth.AuthenticatedUser;
import com.company.cms.auth.CurrentUserProvider;
import com.company.cms.auth.RoleCode;
import com.company.cms.auth.RoleEntity;
import com.company.cms.auth.RoleRepository;
import com.company.cms.auth.UserAccount;
import com.company.cms.auth.UserAccountRepository;
import com.company.cms.common.api.ApiException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRoleAdminService {
    private final UserAccountRepository userAccountRepository;
    private final RoleRepository roleRepository;
    private final CurrentUserProvider currentUserProvider;
    private final AuditLoggingService auditLoggingService;

    public UserRoleAdminService(UserAccountRepository userAccountRepository, RoleRepository roleRepository,
            CurrentUserProvider currentUserProvider, AuditLoggingService auditLoggingService) {
        this.userAccountRepository = userAccountRepository;
        this.roleRepository = roleRepository;
        this.currentUserProvider = currentUserProvider;
        this.auditLoggingService = auditLoggingService;
    }

    @Transactional(readOnly = true)
    public List<UserAccount> list(String q, RoleCode role) {
        return userAccountRepository.search(q, role);
    }

    @Transactional
    public UserAccount replaceRoles(UUID userId, ReplaceRolesRequest request) {
        AuthenticatedUser actor = currentUserProvider.currentUser();
        UserAccount user = userAccountRepository.findDetailedById(userId)
            .orElseThrow(() -> ApiException.notFound("USER_NOT_FOUND", "User was not found."));
        Set<RoleCode> requestedRoles = request.roles() == null || request.roles().isEmpty()
            ? Set.of(RoleCode.EMPLOYEE)
            : request.roles();
        List<RoleEntity> roles = roleRepository.findByCodeIn(requestedRoles);
        if (roles.size() != requestedRoles.size()) {
            throw ApiException.badRequest("ROLE_NOT_FOUND", "One or more roles do not exist.");
        }
        user.replaceRoles(new LinkedHashSet<>(roles));
        userAccountRepository.save(user);
        auditLoggingService.record(actor, AuditAction.ROLE_CHANGE, "User", user.getId(), "User roles replaced.");
        return userAccountRepository.findDetailedById(userId)
            .orElseThrow(() -> ApiException.notFound("USER_NOT_FOUND", "User was not found."));
    }
}
