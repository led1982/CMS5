package com.acme.cms.api.admin;

import com.acme.cms.api.ApiDtos;
import com.acme.cms.api.CmsMapper;
import com.acme.cms.security.AdminAccessService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AccessAdminController {
    private final AdminAccessService access;
    private final CmsMapper mapper;

    public AccessAdminController(AdminAccessService access, CmsMapper mapper) {
        this.access = access;
        this.mapper = mapper;
    }

    @GetMapping("/roles")
    List<ApiDtos.RoleSummary> roles() {
        return access.listRoles().stream().map(mapper::toRoleSummary).toList();
    }

    @PostMapping("/roles")
    ApiDtos.RoleSummary upsertRole(@RequestBody ApiDtos.RoleWriteRequest request) {
        return mapper.toRoleSummary(access.upsertRole(request));
    }

    @GetMapping("/audiences")
    List<ApiDtos.AudienceSummary> audiences() {
        return access.listAudiences().stream().map(mapper::toAudienceSummary).toList();
    }
}
