package com.company.cms.announcement.domain;

import com.company.cms.auth.UserAccount;
import com.company.cms.content.domain.ContentItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "announcement_acknowledgements")
public class AnnouncementAcknowledgement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private ContentItem content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Column(name = "acknowledged_at")
    private Instant acknowledgedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected AnnouncementAcknowledgement() {
    }

    public AnnouncementAcknowledgement(ContentItem content, UserAccount user) {
        this.content = content;
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public ContentItem getContent() {
        return content;
    }

    public UserAccount getUser() {
        return user;
    }

    public Instant getAcknowledgedAt() {
        return acknowledgedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isAcknowledged() {
        return acknowledgedAt != null;
    }

    public void acknowledge() {
        acknowledgedAt = Instant.now();
    }

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }
}
