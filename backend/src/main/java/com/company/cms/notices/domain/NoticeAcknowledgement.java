package com.company.cms.notices.domain;

import java.time.Instant;
import java.util.UUID;

import com.company.cms.content.domain.ContentEnums.AcknowledgementStatus;

public class NoticeAcknowledgement {
    private final UUID id = UUID.randomUUID();
    private final UUID noticeId;
    private final UUID userId;
    private final Instant targetedAt;
    private Instant acknowledgedAt;
    private AcknowledgementStatus status;

    public NoticeAcknowledgement(UUID noticeId, UUID userId) {
        this.noticeId = noticeId;
        this.userId = userId;
        this.targetedAt = Instant.now();
        this.status = AcknowledgementStatus.PENDING;
    }

    public UUID getId() {
        return id;
    }

    public UUID getNoticeId() {
        return noticeId;
    }

    public UUID getUserId() {
        return userId;
    }

    public Instant getTargetedAt() {
        return targetedAt;
    }

    public Instant getAcknowledgedAt() {
        return acknowledgedAt;
    }

    public AcknowledgementStatus getStatus() {
        return status;
    }

    public void acknowledge() {
        this.status = AcknowledgementStatus.ACKNOWLEDGED;
        this.acknowledgedAt = Instant.now();
    }
}
