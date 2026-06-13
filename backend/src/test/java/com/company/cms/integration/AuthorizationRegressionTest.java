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

class AuthorizationRegressionTest {
    @Test
    void directUrlDoesNotExposeUnpublishedContent() {
        ContentItem content = new ContentItem(ContentType.KNOWLEDGE, "Draft", "draft", "", null, null, null);
        AuthenticatedUser user = new AuthenticatedUser(UUID.randomUUID(), "u@example.com", "User", UUID.randomUUID(),
            Set.of(RoleCode.EMPLOYEE));

        boolean visible = new VisibilityService().canView(
            content,
            List.of(new ContentAudience(content, VisibilityType.ALL_EMPLOYEES, null)),
            user,
            Instant.now()
        );

        assertThat(visible).isFalse();
        assertThat(content.getStatus()).isEqualTo(ContentStatus.DRAFT);
    }
}
