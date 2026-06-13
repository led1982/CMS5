package com.company.cms.admin.service;

import com.company.cms.admin.api.AdminDtos.ContentMetrics;
import com.company.cms.admin.api.AdminDtos.TopContent;
import com.company.cms.announcement.domain.AnnouncementAcknowledgementRepository;
import com.company.cms.content.domain.ContentStatus;
import com.company.cms.content.domain.ContentType;
import com.company.cms.content.repository.ContentItemRepository;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContentMetricsService {
    private final ContentItemRepository contentItemRepository;
    private final AnnouncementAcknowledgementRepository acknowledgementRepository;

    public ContentMetricsService(ContentItemRepository contentItemRepository,
            AnnouncementAcknowledgementRepository acknowledgementRepository) {
        this.contentItemRepository = contentItemRepository;
        this.acknowledgementRepository = acknowledgementRepository;
    }

    @Transactional(readOnly = true)
    public ContentMetrics metrics() {
        Map<ContentStatus, Long> byStatus = new LinkedHashMap<>();
        Arrays.stream(ContentStatus.values()).forEach(status -> byStatus.put(status, contentItemRepository.countByStatus(status)));
        Map<ContentType, Long> byType = new LinkedHashMap<>();
        Arrays.stream(ContentType.values()).forEach(type -> byType.put(type, contentItemRepository.countByType(type)));
        List<TopContent> topViewed = contentItemRepository
            .findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "viewCount")))
            .stream()
            .sorted((left, right) -> Integer.compare(right.getViewCount(), left.getViewCount()))
            .map(content -> new TopContent(content.getId(), content.getTitle(), content.getViewCount()))
            .toList();
        long unacknowledged = contentItemRepository.findByStatusAndTypeOrderByPinnedDescPublishedAtDesc(
                ContentStatus.PUBLISHED,
                ContentType.ANNOUNCEMENT
            )
            .stream()
            .mapToLong(acknowledgementRepository::countByContentAndAcknowledgedAtIsNull)
            .sum();
        return new ContentMetrics(byStatus, byType, unacknowledged, topViewed);
    }
}
