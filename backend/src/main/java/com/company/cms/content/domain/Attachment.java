package com.company.cms.content.domain;

import java.time.Instant;
import java.util.UUID;

import com.company.cms.content.domain.ContentEnums.AttachmentScanStatus;
import com.company.cms.content.domain.ContentEnums.AudienceType;

public class Attachment {
    private UUID id = UUID.randomUUID();
    private UUID contentItemId;
    private String fileName;
    private String storageKey;
    private String mimeType;
    private long sizeBytes;
    private AttachmentScanStatus scanStatus = AttachmentScanStatus.PENDING;
    private UUID uploadedBy;
    private Instant uploadedAt = Instant.now();

    public Attachment() {
    }

    public Attachment(String fileName, String mimeType, long sizeBytes, AttachmentScanStatus scanStatus) {
        this.fileName = fileName;
        this.storageKey = "local/" + id + "/" + fileName;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
        this.scanStatus = scanStatus;
    }

    public UUID getId() {
        return id;
    }

    public UUID getContentItemId() {
        return contentItemId;
    }

    public void setContentItemId(UUID contentItemId) {
        this.contentItemId = contentItemId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public AttachmentScanStatus getScanStatus() {
        return scanStatus;
    }

    public UUID getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(UUID uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }
}

class ContentAudience {
    private UUID id = UUID.randomUUID();
    private UUID contentItemId;
    private AudienceType audienceType;
    private String audienceRefId;

    public UUID getId() {
        return id;
    }

    public UUID getContentItemId() {
        return contentItemId;
    }

    public void setContentItemId(UUID contentItemId) {
        this.contentItemId = contentItemId;
    }

    public AudienceType getAudienceType() {
        return audienceType;
    }

    public void setAudienceType(AudienceType audienceType) {
        this.audienceType = audienceType;
    }

    public String getAudienceRefId() {
        return audienceRefId;
    }

    public void setAudienceRefId(String audienceRefId) {
        this.audienceRefId = audienceRefId;
    }
}
