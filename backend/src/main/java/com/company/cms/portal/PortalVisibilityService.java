package com.company.cms.portal;

import com.company.cms.auth.AuthUser;
import com.company.cms.content.domain.ContentEnums.ContentStatus;
import com.company.cms.content.domain.ContentItem;
import org.springframework.stereotype.Service;

@Service
public class PortalVisibilityService {
    public boolean canView(AuthUser user, ContentItem item) {
        if (item.getStatus() != ContentStatus.PUBLISHED) {
            return false;
        }
        return item.getAudiences().stream().anyMatch(audience -> matchesAudience(user, audience));
    }

    private boolean matchesAudience(AuthUser user, String audience) {
        if ("ALL_EMPLOYEES".equals(audience)) {
            return true;
        }
        if (audience.startsWith("DEPARTMENT:")) {
            return audience.substring("DEPARTMENT:".length()).equalsIgnoreCase(user.department());
        }
        if (audience.startsWith("ROLE:")) {
            String role = audience.substring("ROLE:".length());
            return user.roles().stream().anyMatch(item -> item.name().equals(role));
        }
        if (audience.startsWith("USER:")) {
            return audience.substring("USER:".length()).equalsIgnoreCase(user.id().toString());
        }
        return false;
    }
}
