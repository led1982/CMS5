package com.company.cms.analytics;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class AnalyticsQueryRepository {
    private final ContentMetricRecorder recorder;

    public AnalyticsQueryRepository(ContentMetricRecorder recorder) {
        this.recorder = recorder;
    }

    public List<ContentMetricRecorder.MetricEvent> findEvents() {
        return recorder.events();
    }
}
