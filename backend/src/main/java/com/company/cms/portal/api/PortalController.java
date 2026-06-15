package com.company.cms.portal.api;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.company.cms.admin.domain.Category;
import com.company.cms.analytics.ContentMetricRecorder;
import com.company.cms.auth.AuthUser;
import com.company.cms.content.domain.ContentEnums.ContentType;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.service.ContentService;
import com.company.cms.portal.BookmarkService;
import com.company.cms.portal.PortalVisibilityService;
import com.company.cms.portal.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/portal")
public class PortalController {
    private static final List<Category> CATEGORY_SHORTCUTS = List.of(
            new Category(UUID.fromString("10000000-0000-0000-0000-000000000001"), "Security", "security", "보안 정책과 사고 대응", 1),
            new Category(UUID.fromString("10000000-0000-0000-0000-000000000002"), "Engineering", "engineering", "개발 표준과 릴리스 운영", 2),
            new Category(UUID.fromString("10000000-0000-0000-0000-000000000003"), "HR", "hr", "인사 제도와 복리후생", 3),
            new Category(UUID.fromString("10000000-0000-0000-0000-000000000004"), "Policy", "policy", "전사 운영 정책", 4)
    );

    private final SearchService searchService;
    private final ContentService contentService;
    private final PortalVisibilityService visibilityService;
    private final BookmarkService bookmarkService;
    private final ContentMetricRecorder metricRecorder;

    public PortalController(SearchService searchService, ContentService contentService, PortalVisibilityService visibilityService, BookmarkService bookmarkService, ContentMetricRecorder metricRecorder) {
        this.searchService = searchService;
        this.contentService = contentService;
        this.visibilityService = visibilityService;
        this.bookmarkService = bookmarkService;
        this.metricRecorder = metricRecorder;
    }

    @GetMapping("/home")
    Map<String, Object> home(@AuthenticationPrincipal AuthUser user) {
        List<ContentItem> visible = searchService.visibleContent(user);
        List<ContentItem> requiredNotices = visible.stream()
                .filter(ContentItem::isRequiresAcknowledgement)
                .limit(3)
                .toList();
        return Map.of(
                "requiredNotices", requiredNotices,
                "latestUpdates", visible.stream().limit(5).toList(),
                "bookmarks", bookmarkService.list(user).stream().limit(5).toList(),
                "popularContent", visible.stream().limit(5).toList(),
                "categoryShortcuts", CATEGORY_SHORTCUTS
        );
    }

    @GetMapping("/search")
    Map<String, Object> search(@RequestParam String q, @RequestParam(required = false) ContentType contentType, @AuthenticationPrincipal AuthUser user) {
        List<ContentItem> results = searchService.search(user, q, contentType);
        metricRecorder.recordSearch(user, q, results.size());
        return Map.of("query", q, "resultCount", results.size(), "items", results);
    }

    @GetMapping("/content/{contentId}")
    ContentItem content(@PathVariable UUID contentId, @AuthenticationPrincipal AuthUser user) {
        ContentItem item = contentService.get(contentId);
        if (!visibilityService.canView(user, item)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "콘텐츠 접근 권한이 없습니다.");
        }
        metricRecorder.recordView(user, contentId);
        return item;
    }

    @GetMapping("/bookmarks")
    List<ContentItem> bookmarks(@AuthenticationPrincipal AuthUser user) {
        return bookmarkService.list(user);
    }

    @PostMapping("/bookmarks")
    @ResponseStatus(HttpStatus.CREATED)
    void addBookmark(@RequestBody BookmarkService.BookmarkRequest request, @AuthenticationPrincipal AuthUser user) {
        bookmarkService.add(user, request.contentId());
    }

    @DeleteMapping("/bookmarks/{contentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeBookmark(@PathVariable UUID contentId, @AuthenticationPrincipal AuthUser user) {
        bookmarkService.remove(user, contentId);
    }
}
