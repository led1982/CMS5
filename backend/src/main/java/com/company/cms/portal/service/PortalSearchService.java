package com.company.cms.portal.service;

import com.company.cms.auth.AuthenticatedUser;
import com.company.cms.auth.CurrentUserProvider;
import com.company.cms.common.observability.CmsObservabilityService;
import com.company.cms.content.api.ContentDtos.PagedContentSummary;
import com.company.cms.content.api.ContentMapper;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.domain.ContentStatus;
import com.company.cms.content.domain.ContentType;
import com.company.cms.content.domain.ContentVersion;
import com.company.cms.content.repository.ContentAudienceRepository;
import com.company.cms.content.repository.ContentItemRepository;
import com.company.cms.content.repository.ContentVersionRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PortalSearchService {
    private final ContentItemRepository contentItemRepository;
    private final ContentVersionRepository contentVersionRepository;
    private final ContentAudienceRepository audienceRepository;
    private final CurrentUserProvider currentUserProvider;
    private final VisibilityService visibilityService;
    private final ContentMapper mapper;
    private final CmsObservabilityService observabilityService;

    public PortalSearchService(ContentItemRepository contentItemRepository,
            ContentVersionRepository contentVersionRepository,
            ContentAudienceRepository audienceRepository,
            CurrentUserProvider currentUserProvider,
            VisibilityService visibilityService,
            ContentMapper mapper,
            CmsObservabilityService observabilityService) {
        this.contentItemRepository = contentItemRepository;
        this.contentVersionRepository = contentVersionRepository;
        this.audienceRepository = audienceRepository;
        this.currentUserProvider = currentUserProvider;
        this.visibilityService = visibilityService;
        this.mapper = mapper;
        this.observabilityService = observabilityService;
    }

    @Transactional(readOnly = true)
    public PagedContentSummary search(String q, ContentType type, UUID categoryId, String tag,
            UUID ownerDepartmentId, int page, int size, String sort) {
        Instant started = Instant.now();
        AuthenticatedUser user = currentUserProvider.currentUser();
        String keyword = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        List<ContentItem> visible = contentItemRepository.findTop20ByStatusOrderByUpdatedAtDesc(ContentStatus.PUBLISHED)
            .stream()
            .filter(item -> type == null || item.getType() == type)
            .filter(item -> categoryId == null || item.getCategory().getId().equals(categoryId))
            .filter(item -> ownerDepartmentId == null
                || (item.getOwnerDepartment() != null && item.getOwnerDepartment().getId().equals(ownerDepartmentId)))
            .filter(item -> tag == null || item.getTags().stream().anyMatch(t -> t.getName().equalsIgnoreCase(tag)))
            .filter(item -> visibilityService.canView(item, audienceRepository.findByContent(item), user, Instant.now()))
            .filter(item -> matchesKeyword(item, keyword))
            .sorted(comparator(sort))
            .toList();
        int normalizedSize = Math.min(Math.max(size, 1), 100);
        int from = Math.min(Math.max(page, 0) * normalizedSize, visible.size());
        int to = Math.min(from + normalizedSize, visible.size());
        observabilityService.searchEvent(q, visible.size(), Duration.between(started, Instant.now()));
        return new PagedContentSummary(
            visible.subList(from, to).stream().map(item -> mapper.summary(item, false)).toList(),
            Math.max(page, 0),
            normalizedSize,
            visible.size(),
            (int) Math.ceil((double) visible.size() / normalizedSize)
        );
    }

    private boolean matchesKeyword(ContentItem item, String keyword) {
        if (keyword.isBlank()) {
            return true;
        }
        ContentVersion version = contentVersionRepository.findTopByContentOrderByVersionNumberDesc(item).orElse(null);
        String haystack = String.join(" ",
            item.getTitle(),
            item.getSummary() == null ? "" : item.getSummary(),
            version == null ? "" : version.getBody(),
            item.getTags().stream().map(tag -> tag.getName()).toList().toString()
        ).toLowerCase(Locale.ROOT);
        return haystack.contains(keyword);
    }

    private Comparator<ContentItem> comparator(String sort) {
        if ("MOST_VIEWED".equalsIgnoreCase(sort)) {
            return Comparator.comparing(ContentItem::getViewCount).reversed();
        }
        if ("LATEST".equalsIgnoreCase(sort)) {
            return Comparator.comparing(ContentItem::getPublishedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed();
        }
        return Comparator
            .comparing(ContentItem::isPinned).reversed()
            .thenComparing(Comparator.comparing(ContentItem::getPublishedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
    }
}
