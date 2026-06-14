package com.company.cms.portal;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.company.cms.auth.AuthUser;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.service.ContentService;
import org.springframework.stereotype.Service;

@Service
public class BookmarkService {
    private final ConcurrentMap<UUID, Set<UUID>> bookmarks = new ConcurrentHashMap<>();
    private final ContentService contentService;
    private final PortalVisibilityService visibilityService;

    public BookmarkService(ContentService contentService, PortalVisibilityService visibilityService) {
        this.contentService = contentService;
        this.visibilityService = visibilityService;
    }

    public List<ContentItem> list(AuthUser user) {
        return bookmarks.getOrDefault(user.id(), Set.of()).stream()
                .map(contentService::get)
                .filter(item -> visibilityService.canView(user, item))
                .toList();
    }

    public void add(AuthUser user, UUID contentId) {
        ContentItem item = contentService.get(contentId);
        if (visibilityService.canView(user, item)) {
            bookmarks.computeIfAbsent(user.id(), ignored -> new LinkedHashSet<>()).add(contentId);
        }
    }

    public void remove(AuthUser user, UUID contentId) {
        bookmarks.computeIfAbsent(user.id(), ignored -> new LinkedHashSet<>()).remove(contentId);
    }

    public record BookmarkRequest(UUID contentId) {
    }
}
