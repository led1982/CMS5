package com.company.cms.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.cms.auth.AuthenticatedUser;
import com.company.cms.auth.RoleCode;
import com.company.cms.content.domain.ContentAudience;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.domain.ContentStatus;
import com.company.cms.content.domain.ContentType;
import com.company.cms.content.domain.VisibilityType;
import com.company.cms.portal.service.VisibilityService;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PortalSearchAuthorizationIntegrationTest {
    @Test
    void filtersContentByAudience() {
        ContentItem content = new ContentItem(ContentType.DOCUMENT, "HR only", "hr-only", "", null, null, null);
        content.setStatus(ContentStatus.PUBLISHED);
        UUID hrDepartment = UUID.fromString("10000000-0000-0000-0000-000000000001");
        AuthenticatedUser engineeringUser = new AuthenticatedUser(
            UUID.randomUUID(),
            "employee@example.com",
            "Employee",
            UUID.fromString("10000000-0000-0000-0000-000000000002"),
            Set.of(RoleCode.EMPLOYEE)
        );

        boolean visible = new VisibilityService().canView(
            content,
            List.of(new ContentAudience(content, VisibilityType.DEPARTMENT, hrDepartment)),
            engineeringUser,
            Instant.now()
        );

        assertThat(visible).isFalse();
    }
}
