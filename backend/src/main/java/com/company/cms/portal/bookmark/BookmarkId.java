package com.company.cms.portal.bookmark;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class BookmarkId implements Serializable {
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "content_id")
    private UUID contentId;

    protected BookmarkId() {
    }

    public BookmarkId(UUID userId, UUID contentId) {
        this.userId = userId;
        this.contentId = contentId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getContentId() {
        return contentId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BookmarkId that)) {
            return false;
        }
        return Objects.equals(userId, that.userId) && Objects.equals(contentId, that.contentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, contentId);
    }
}
