package com.company.cms.common.observability;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CmsObservabilityService {
    private static final Logger log = LoggerFactory.getLogger(CmsObservabilityService.class);

    public void workflowEvent(String action, String contentId) {
        log.info("content.workflow action={} contentId={}", action, contentId);
    }

    public void searchEvent(String query, int resultCount, Duration elapsed) {
        log.info("portal.search queryLength={} resultCount={} elapsedMs={}",
            query == null ? 0 : query.length(),
            resultCount,
            elapsed.toMillis());
    }
}
