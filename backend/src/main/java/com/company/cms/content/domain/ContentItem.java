package com.company.cms.content.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.company.cms.content.domain.ContentEnums.ContentStatus;
import com.company.cms.content.domain.ContentEnums.ContentType;
import com.company.cms.shared.domain.AuditableEntity;

public class ContentItem extends AuditableEntity {
    private ContentType contentType;
    private ContentStatus status = ContentStatus.DRAFT;
    private String title;
    private String slug;
    private String summary;
    private String body;
    private UUID categoryId;
    private String categoryName;
    private UUID authorId;
    private String authorName;
    private boolean important;
    private boolean requiresAcknowledgement;
    private Instant publishedAt;
    private Instant scheduledAt;
    private Instant expiresAt;
    private Instant archivedAt;
    private int versionNumber = 1;
    private final List<String> tags = new ArrayList<>();
    private final List<String> audiences = new ArrayList<>();
    private final List<Attachment> attachments = new ArrayList<>();

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public ContentStatus getStatus() {
        return status;
    }

    public void setStatus(ContentStatus status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public boolean isRequiresAcknowledgement() {
        return requiresAcknowledgement;
    }

    public void setRequiresAcknowledgement(boolean requiresAcknowledgement) {
        this.requiresAcknowledgement = requiresAcknowledgement;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Instant scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(Instant archivedAt) {
        this.archivedAt = archivedAt;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void incrementVersion() {
        this.versionNumber++;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getAudiences() {
        return audiences;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }
}
