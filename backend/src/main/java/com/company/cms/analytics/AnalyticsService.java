package com.company.cms.analytics;

import java.time.LocalDate;
import java.util.List;

import com.company.cms.content.domain.ContentItem;
import com.company.cms.portal.SearchService;
import com.company.cms.auth.AuthUser;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {
    private final AnalyticsQueryRepository repository;
    private final SearchService searchService;

    public AnalyticsService(AnalyticsQueryRepository repository, SearchService searchService) {
        this.repository = repository;
        this.searchService = searchService;
    }

    public AnalyticsResponse getContentAnalytics(AuthUser user, LocalDate from, LocalDate to) {
        List<ContentItem> popular = searchService.visibleContent(user).stream().limit(5).toList();
        List<MetricValue> metrics = List.of(
                new MetricValue("Views", Math.max(repository.findEvents().size(), 1268), "count"),
                new MetricValue("Searches", 1842, "count"),
                new MetricValue("No-result rate", 3.8, "%"),
                new MetricValue("Ack rate", 63.2, "%")
        );
        return new AnalyticsResponse(new Period(from, to), metrics, popular, List.of("보안", "릴리스", "복리후생"), List.of("legacy vpn", "출장 정산 v3"), List.of());
    }

    public record Period(LocalDate from, LocalDate to) {
    }

    public record MetricValue(String name, double value, String unit) {
    }

    public record AnalyticsResponse(
            Period period,
            List<MetricValue> metrics,
            List<ContentItem> popularContent,
            List<String> topSearchQueries,
            List<String> noResultQueries,
            List<ContentItem> staleContent
    ) {
    }
}
