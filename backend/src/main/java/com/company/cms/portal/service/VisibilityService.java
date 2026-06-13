package com.company.cms.portal.service;

import com.company.cms.auth.AuthenticatedUser;
import com.company.cms.auth.RoleCode;
import com.company.cms.content.domain.ContentAudience;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.domain.ContentStatus;
import com.company.cms.content.domain.VisibilityType;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class VisibilityService {
    private static final Map<RoleCode, UUID> ROLE_IDS = Map.of(
        RoleCode.ADMIN, UUID.fromString("20000000-0000-0000-0000-000000000001"),
        RoleCode.EDITOR, UUID.fromString("20000000-0000-0000-0000-000000000002"),
        RoleCode.REVIEWER, UUID.fromString("20000000-0000-0000-0000-000000000003"),
        RoleCode.EMPLOYEE, UUID.fromString("20000000-0000-0000-0000-000000000004")
    );

    public boolean canView(ContentItem content, List<ContentAudience> audiences, AuthenticatedUser user, Instant now) {
        if (content.getStatus() != ContentStatus.PUBLISHED) {
            return false;
        }
        if (content.getPublishStartAt() != null && content.getPublishStartAt().isAfter(now)) {
            return false;
        }
        if (content.getPublishEndAt() != null && !content.getPublishEndAt().isAfter(now)) {
            return false;
        }
        if (audiences == null || audiences.isEmpty()) {
            return false;
        }
        return audiences.stream().anyMatch(audience -> matches(audience, user));
    }

    private boolean matches(ContentAudience audience, AuthenticatedUser user) {
        VisibilityType type = audience.getVisibilityType();
        UUID targetId = audience.getTargetId();
        return switch (type) {
            case ALL_EMPLOYEES -> true;
            case DEPARTMENT -> targetId != null && targetId.equals(user.departmentId());
            case USER -> targetId != null && targetId.equals(user.id());
            case ROLE -> targetId != null && user.roles().stream().map(ROLE_IDS::get).anyMatch(targetId::equals);
            case GROUP -> false;
        };
    }
}
