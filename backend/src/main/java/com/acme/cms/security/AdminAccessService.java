package com.acme.cms.security;

import com.acme.cms.api.ApiDtos;
import com.acme.cms.audit.AuditEventService;
import com.acme.cms.security.model.AudienceGroup;
import com.acme.cms.security.model.RoleEntity;
import com.acme.cms.security.repository.AudienceGroupRepository;
import com.acme.cms.security.repository.RoleRepository;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminAccessService {
    private final RoleRepository roles;
    private final AudienceGroupRepository audiences;
    private final CurrentUser currentUser;
    private final AuditEventService audit;

    public AdminAccessService(
        RoleRepository roles,
        AudienceGroupRepository audiences,
        CurrentUser currentUser,
        AuditEventService audit
    ) {
        this.roles = roles;
        this.audiences = audiences;
        this.currentUser = currentUser;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public List<RoleEntity> listRoles() {
        currentUser.requirePermission("ADMIN_ACCESS");
        return roles.findAll().stream().sorted(java.util.Comparator.comparing(RoleEntity::getCode)).toList();
    }

    @Transactional
    public RoleEntity upsertRole(ApiDtos.RoleWriteRequest request) {
        currentUser.requirePermission("ADMIN_ACCESS");
        RoleEntity role = roles.findByCode(request.code()).orElseGet(RoleEntity::new);
        role.setCode(request.code());
        role.setName(request.name());
        role.setDescription(request.description());
        role.setPermissions(new LinkedHashSet<>(request.permissions() == null ? List.of() : request.permissions()));
        role = roles.save(role);
        audit.record(currentUser.get(), "ROLE_SAVED", "ROLE", role.getId().toString(), role.getCode());
        return role;
    }

    @Transactional(readOnly = true)
    public List<AudienceGroup> listAudiences() {
        currentUser.requirePermission("ADMIN_ACCESS");
        return audiences.findAll().stream().sorted(java.util.Comparator.comparing(AudienceGroup::getName)).toList();
    }
}
