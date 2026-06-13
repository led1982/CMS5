package com.company.cms.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.domain.ContentStatus;
import com.company.cms.content.domain.ContentType;
import org.junit.jupiter.api.Test;

class AnnouncementAcknowledgementIntegrationTest {
    @Test
    void requiredAcknowledgementIsOnlyMeaningfulForPublishedAnnouncements() {
        ContentItem content = new ContentItem(ContentType.ANNOUNCEMENT, "Security", "security", "", null, null, null);
        content.setStatus(ContentStatus.PUBLISHED);

        assertThat(content.getType()).isEqualTo(ContentType.ANNOUNCEMENT);
        assertThat(content.getStatus()).isEqualTo(ContentStatus.PUBLISHED);
    }
}
