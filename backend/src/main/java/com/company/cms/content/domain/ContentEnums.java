package com.company.cms.content.domain;

public final class ContentEnums {
    private ContentEnums() {
    }

    public enum ContentType {
        ARTICLE, DOCUMENT, NOTICE
    }

    public enum ContentStatus {
        DRAFT, IN_REVIEW, REJECTED, APPROVED, SCHEDULED, PUBLISHED, ARCHIVED, EXPIRED
    }

    public enum AudienceType {
        ALL_EMPLOYEES, DEPARTMENT, ROLE, USER
    }

    public enum AttachmentScanStatus {
        PENDING, CLEAN, INFECTED, FAILED
    }

    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED, CANCELLED
    }

    public enum ApprovalDecision {
        APPROVE, REJECT
    }

    public enum PublicationStatus {
        SCHEDULED, PUBLISHED, ARCHIVED, EXPIRED
    }

    public enum AcknowledgementStatus {
        PENDING, ACKNOWLEDGED, EXEMPTED
    }
}
