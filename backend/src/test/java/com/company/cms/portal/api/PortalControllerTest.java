package com.company.cms.portal.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.company.cms.analytics.ContentMetricRecorder;
import com.company.cms.auth.AuthUser;
import com.company.cms.auth.RoleCode;
import com.company.cms.content.domain.ContentEnums.ContentStatus;
import com.company.cms.content.domain.ContentEnums.ContentType;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.service.ContentService;
import com.company.cms.portal.BookmarkService;
import com.company.cms.portal.PortalVisibilityService;
import com.company.cms.portal.SearchService;
import org.junit.jupiter.api.Test;

class PortalControllerTest {
    private final SearchService searchService = mock(SearchService.class);
    private final ContentService contentService = mock(ContentService.class);
    private final PortalVisibilityService visibilityService = mock(PortalVisibilityService.class);
    private final BookmarkService bookmarkService = mock(BookmarkService.class);
    private final ContentMetricRecorder metricRecorder = mock(ContentMetricRecorder.class);
    private final PortalController controller = new PortalController(searchService, contentService, visibilityService, bookmarkService, metricRecorder);

    @Test
    void homeIncludesDashboardSectionsAndBookmarks() {
        AuthUser user = new AuthUser(UUID.randomUUID(), "employee@example.com", "일반 사용자", "Engineering", Set.of(RoleCode.EMPLOYEE));
        ContentItem notice = content("분기 보안 교육 확인 요청", ContentType.NOTICE, true);
        ContentItem article = content("계정 보안 기준 개정 안내", ContentType.ARTICLE, false);

        when(searchService.visibleContent(user)).thenReturn(List.of(notice, article));
        when(bookmarkService.list(user)).thenReturn(List.of(article));

        Map<String, Object> home = controller.home(user);

        assertThat(home).containsKeys("requiredNotices", "latestUpdates", "bookmarks", "popularContent", "categoryShortcuts");
        assertThat(home.get("requiredNotices")).isEqualTo(List.of(notice));
        assertThat(home.get("latestUpdates")).isEqualTo(List.of(notice, article));
        assertThat(home.get("bookmarks")).isEqualTo(List.of(article));
        assertThat((List<?>) home.get("categoryShortcuts")).isNotEmpty();
    }

    private ContentItem content(String title, ContentType type, boolean requiresAcknowledgement) {
        ContentItem item = new ContentItem();
        item.setContentType(type);
        item.setStatus(ContentStatus.PUBLISHED);
        item.setTitle(title);
        item.setSlug(title);
        item.setSummary(title);
        item.setBody(title);
        item.setCategoryName("Security");
        item.setRequiresAcknowledgement(requiresAcknowledgement);
        return item;
    }
}
