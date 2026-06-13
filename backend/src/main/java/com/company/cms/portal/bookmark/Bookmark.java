package com.company.cms.portal.bookmark;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "bookmarks")
public class Bookmark {
    @EmbeddedId
    private BookmarkId id;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Bookmark() {
    }

    public Bookmark(BookmarkId id) {
        this.id = id;
    }

    public BookmarkId getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }
}
