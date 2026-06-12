package com.acme.cms.content.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notice_settings")
public class NoticeSettings {
    @Id
    @Column(name = "content_item_id")
    private UUID contentItemId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "content_item_id")
    private ContentItem contentItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticePriority priority = NoticePriority.NORMAL;

    @Column(nullable = false)
    private boolean requiresAcknowledgement;

    private Instant acknowledgementDueAt;

    public UUID getContentItemId() {
        return contentItemId;
    }

    public void setContentItemId(UUID contentItemId) {
        this.contentItemId = contentItemId;
    }

    public ContentItem getContentItem() {
        return contentItem;
    }

    public void setContentItem(ContentItem contentItem) {
        this.contentItem = contentItem;
    }

    public NoticePriority getPriority() {
        return priority;
    }

    public void setPriority(NoticePriority priority) {
        this.priority = priority;
    }

    public boolean isRequiresAcknowledgement() {
        return requiresAcknowledgement;
    }

    public void setRequiresAcknowledgement(boolean requiresAcknowledgement) {
        this.requiresAcknowledgement = requiresAcknowledgement;
    }

    public Instant getAcknowledgementDueAt() {
        return acknowledgementDueAt;
    }

    public void setAcknowledgementDueAt(Instant acknowledgementDueAt) {
        this.acknowledgementDueAt = acknowledgementDueAt;
    }
}
