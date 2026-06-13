package com.company.cms.content.domain;

import com.company.cms.auth.Department;
import com.company.cms.auth.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "content_items")
public class ContentItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentStatus status = ContentStatus.DRAFT;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(length = 500)
    private String summary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_department_id")
    private Department ownerDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserAccount author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private UserAccount reviewer;

    @Column(name = "current_version_id")
    private UUID currentVersionId;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "publish_start_at")
    private Instant publishStartAt;

    @Column(name = "publish_end_at")
    private Instant publishEndAt;

    @Column(nullable = false)
    private boolean pinned;

    @Column(name = "requires_acknowledgement", nullable = false)
    private boolean requiresAcknowledgement;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToMany
    @JoinTable(
        name = "content_tags",
        joinColumns = @JoinColumn(name = "content_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new LinkedHashSet<>();

    protected ContentItem() {
    }

    public ContentItem(ContentType type, String title, String slug, String summary, Category category,
            Department ownerDepartment, UserAccount author) {
        this.type = type;
        this.title = title;
        this.slug = slug;
        this.summary = summary;
        this.category = category;
        this.ownerDepartment = ownerDepartment;
        this.author = author;
    }

    public UUID getId() {
        return id;
    }

    public ContentType getType() {
        return type;
    }

    public ContentStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public String getSummary() {
        return summary;
    }

    public Category getCategory() {
        return category;
    }

    public Department getOwnerDepartment() {
        return ownerDepartment;
    }

    public UserAccount getAuthor() {
        return author;
    }

    public UserAccount getReviewer() {
        return reviewer;
    }

    public UUID getCurrentVersionId() {
        return currentVersionId;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public Instant getPublishStartAt() {
        return publishStartAt;
    }

    public Instant getPublishEndAt() {
        return publishEndAt;
    }

    public boolean isPinned() {
        return pinned;
    }

    public boolean isRequiresAcknowledgement() {
        return requiresAcknowledgement;
    }

    public int getViewCount() {
        return viewCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void updateDraft(ContentType type, String title, String slug, String summary, Category category,
            Department ownerDepartment, boolean pinned, boolean requiresAcknowledgement, Set<Tag> tags) {
        this.type = type;
        this.title = title;
        this.slug = slug;
        this.summary = summary;
        this.category = category;
        this.ownerDepartment = ownerDepartment;
        this.pinned = pinned;
        this.requiresAcknowledgement = requiresAcknowledgement;
        this.tags.clear();
        this.tags.addAll(tags);
    }

    public void setCurrentVersionId(UUID currentVersionId) {
        this.currentVersionId = currentVersionId;
    }

    public void setStatus(ContentStatus status) {
        this.status = status;
    }

    public void assignReviewer(UserAccount reviewer) {
        this.reviewer = reviewer;
    }

    public void publish(Instant publishedAt, Instant publishStartAt, Instant publishEndAt) {
        this.status = ContentStatus.PUBLISHED;
        this.publishedAt = publishedAt;
        this.publishStartAt = publishStartAt;
        this.publishEndAt = publishEndAt;
    }

    public void incrementViewCount() {
        this.viewCount += 1;
    }

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
