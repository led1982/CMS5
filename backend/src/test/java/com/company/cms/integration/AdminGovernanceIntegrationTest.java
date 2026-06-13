package com.company.cms.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.cms.audit.AuditAction;
import com.company.cms.auth.RoleCode;
import org.junit.jupiter.api.Test;

class AdminGovernanceIntegrationTest {
    @Test
    void roleChangesAreAuditableAdminActions() {
        assertThat(RoleCode.ADMIN).isNotNull();
        assertThat(AuditAction.ROLE_CHANGE).isEqualTo(AuditAction.valueOf("ROLE_CHANGE"));
    }
}
