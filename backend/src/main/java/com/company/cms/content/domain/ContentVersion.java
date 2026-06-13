package com.company.cms.content.domain;

import java.time.Instant;
import java.util.UUID;

public class ContentVersion {
    private final UUID id = UUID.randomUUID();
    private final UUID contentItemId;
    private final int versionNumber;
    private final String titleSnapshot;
    private final String summarySnapshot;
    private final String bodySnapshot;
    private final String changeSummary;
    private final UUID createdBy;
    private final Instant createdAt = Instant.now();

    public ContentVersion(UUID contentItemId, int versionNumber, String titleSnapshot, String summarySnapshot, String bodySnapshot, String changeSummary, UUID createdBy) {
        this.contentItemId = contentItemId;
        this.versionNumber = versionNumber;
        this.titleSnapshot = titleSnapshot;
        this.summarySnapshot = summarySnapshot;
        this.bodySnapshot = bodySnapshot;
        this.changeSummary = changeSummary;
        this.createdBy = createdBy;
    }

    public UUID getId() {
        return id;
    }

    public UUID getContentItemId() {
        return contentItemId;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public String getTitleSnapshot() {
        return titleSnapshot;
    }

    public String getSummarySnapshot() {
        return summarySnapshot;
    }

    public String getBodySnapshot() {
        return bodySnapshot;
    }

    public String getChangeSummary() {
        return changeSummary;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
