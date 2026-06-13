package com.company.cms.content.domain;

import com.company.cms.auth.UserAccount;
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
@Table(name = "content_versions")
public class ContentVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private ContentItem content;

    @Column(name = "version_number", nullable = false)
    private int versionNumber;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 500)
    private String summary;

    @Column(nullable = false, columnDefinition = "text")
    private String body;

    @Column(name = "change_note", length = 500)
    private String changeNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private UserAccount createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ContentVersion() {
    }

    public ContentVersion(ContentItem content, int versionNumber, String title, String summary, String body,
            String changeNote, UserAccount createdBy) {
        this.content = content;
        this.versionNumber = versionNumber;
        this.title = title;
        this.summary = summary;
        this.body = body;
        this.changeNote = changeNote;
        this.createdBy = createdBy;
    }

    public UUID getId() {
        return id;
    }

    public ContentItem getContent() {
        return content;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getBody() {
        return body;
    }

    public String getChangeNote() {
        return changeNote;
    }

    public UserAccount getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }
}
