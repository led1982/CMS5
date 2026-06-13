package com.company.cms.content.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "content_audiences")
public class ContentAudience {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private ContentItem content;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility_type", nullable = false)
    private VisibilityType visibilityType;

    @Column(name = "target_id")
    private UUID targetId;

    protected ContentAudience() {
    }

    public ContentAudience(ContentItem content, VisibilityType visibilityType, UUID targetId) {
        this.content = content;
        this.visibilityType = visibilityType;
        this.targetId = targetId;
    }

    public UUID getId() {
        return id;
    }

    public ContentItem getContent() {
        return content;
    }

    public VisibilityType getVisibilityType() {
        return visibilityType;
    }

    public UUID getTargetId() {
        return targetId;
    }
}
