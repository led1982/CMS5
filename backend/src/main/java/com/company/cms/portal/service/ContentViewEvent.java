package com.company.cms.portal.service;

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
@Table(name = "content_view_events")
public class ContentViewEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private ContentItem content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Column(name = "viewed_at", nullable = false)
    private Instant viewedAt;

    private String source;

    protected ContentViewEvent() {
    }

    public ContentViewEvent(ContentItem content, UserAccount user, String source) {
        this.content = content;
        this.user = user;
        this.source = source;
    }

    @PrePersist
    void prePersist() {
        viewedAt = Instant.now();
    }
}
