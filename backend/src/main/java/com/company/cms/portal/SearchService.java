package com.company.cms.portal;

import java.util.Comparator;
import java.util.List;

import com.company.cms.auth.AuthUser;
import com.company.cms.content.domain.ContentEnums.ContentType;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.service.ContentService;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
    private final ContentService contentService;
    private final PortalVisibilityService visibilityService;

    public SearchService(ContentService contentService, PortalVisibilityService visibilityService) {
        this.contentService = contentService;
        this.visibilityService = visibilityService;
    }

    public List<ContentItem> search(AuthUser user, String query, ContentType type) {
        return contentService.list(type, null, query).stream()
                .filter(item -> visibilityService.canView(user, item))
                .sorted(Comparator.comparing(ContentItem::isImportant).reversed().thenComparing(ContentItem::getUpdatedAt).reversed())
                .toList();
    }

    public List<ContentItem> visibleContent(AuthUser user) {
        return contentService.list(null, null, null).stream()
                .filter(item -> visibilityService.canView(user, item))
                .sorted(Comparator.comparing(ContentItem::getUpdatedAt).reversed())
                .toList();
    }
}
