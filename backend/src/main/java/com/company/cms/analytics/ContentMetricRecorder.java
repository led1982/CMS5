package com.company.cms.analytics;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.company.cms.auth.AuthUser;
import org.springframework.stereotype.Component;

@Component
public class ContentMetricRecorder {
    private final CopyOnWriteArrayList<MetricEvent> events = new CopyOnWriteArrayList<>();

    public void recordView(AuthUser user, UUID contentId) {
        events.add(new MetricEvent(UUID.randomUUID(), contentId, user.id(), "VIEW", Instant.now()));
    }

    public void recordSearch(AuthUser user, String query, int resultCount) {
        events.add(new MetricEvent(UUID.randomUUID(), null, user.id(), "SEARCH:" + query + ":" + resultCount, Instant.now()));
    }

    public void recordAcknowledgement(AuthUser user, UUID noticeId) {
        events.add(new MetricEvent(UUID.randomUUID(), noticeId, user.id(), "ACKNOWLEDGEMENT", Instant.now()));
    }

    public List<MetricEvent> events() {
        return List.copyOf(events);
    }

    public record MetricEvent(UUID id, UUID contentId, UUID userId, String metricType, Instant occurredAt) {
    }
}
