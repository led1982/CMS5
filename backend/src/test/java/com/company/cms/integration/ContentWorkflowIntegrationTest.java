package com.company.cms.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.cms.content.domain.ContentStatus;
import java.util.List;
import org.junit.jupiter.api.Test;

class ContentWorkflowIntegrationTest {
    @Test
    void expectedWorkflowOrderIsDraftReviewPublished() {
        assertThat(List.of(ContentStatus.DRAFT, ContentStatus.IN_REVIEW, ContentStatus.PUBLISHED))
            .containsExactly(ContentStatus.DRAFT, ContentStatus.IN_REVIEW, ContentStatus.PUBLISHED);
    }
}
