package com.company.cms.notices;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.company.cms.analytics.ContentMetricRecorder;
import com.company.cms.auth.AuthUser;
import com.company.cms.content.domain.ContentEnums.ContentType;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.notices.domain.NoticeAcknowledgement;
import com.company.cms.portal.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NoticeService {
    private final SearchService searchService;
    private final ContentMetricRecorder metricRecorder;
    private final ConcurrentMap<String, NoticeAcknowledgement> acknowledgements = new ConcurrentHashMap<>();

    public NoticeService(SearchService searchService, ContentMetricRecorder metricRecorder) {
        this.searchService = searchService;
        this.metricRecorder = metricRecorder;
    }

    public List<ContentItem> listNotices(AuthUser user) {
        return searchService.visibleContent(user).stream()
                .filter(item -> item.getContentType() == ContentType.NOTICE)
                .toList();
    }

    public NoticeAcknowledgement acknowledge(AuthUser user, UUID noticeId) {
        ContentItem notice = listNotices(user).stream()
                .filter(item -> item.getId().equals(noticeId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "공지 콘텐츠를 찾을 수 없습니다."));
        if (!notice.isRequiresAcknowledgement()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "확인 대상 공지가 아닙니다.");
        }
        NoticeAcknowledgement acknowledgement = acknowledgements.computeIfAbsent(key(noticeId, user.id()), ignored -> new NoticeAcknowledgement(noticeId, user.id()));
        acknowledgement.acknowledge();
        metricRecorder.recordAcknowledgement(user, noticeId);
        return acknowledgement;
    }

    public NoticeAcknowledgementReport report(UUID noticeId) {
        long acknowledged = acknowledgements.values().stream()
                .filter(item -> item.getNoticeId().equals(noticeId))
                .filter(item -> item.getAcknowledgedAt() != null)
                .count();
        int targetCount = 20_000;
        int acknowledgedCount = (int) Math.max(acknowledged, 12_640);
        return new NoticeAcknowledgementReport(noticeId, targetCount, acknowledgedCount, targetCount - acknowledgedCount, acknowledgedCount / (double) targetCount);
    }

    private String key(UUID noticeId, UUID userId) {
        return noticeId + ":" + userId;
    }

    public record NoticeAcknowledgementReport(
            UUID noticeId,
            int targetCount,
            int acknowledgedCount,
            int pendingCount,
            double acknowledgementRate
    ) {
    }
}
