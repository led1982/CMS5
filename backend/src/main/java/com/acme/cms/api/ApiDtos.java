package com.acme.cms.api;

import com.acme.cms.content.model.ContentStatus;
import com.acme.cms.content.model.ContentType;
import com.acme.cms.content.model.NoticePriority;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class ApiDtos {
    private ApiDtos() {
    }

    public record FieldError(String field, String message) {
    }

    public record ErrorResponse(String code, String message, List<FieldError> fieldErrors) {
    }

    public record RoleSummary(String id, String code, String name, List<String> permissions) {
    }

    public record UserSummary(
        String id,
        String email,
        String displayName,
        String department,
        List<RoleSummary> roles,
        List<String> permissions
    ) {
    }

    public record AudienceSummary(String id, String code, String name, String type, boolean active) {
    }

    public record CategoryDto(
        String id,
        String parentId,
        String name,
        String slug,
        String description,
        int sortOrder,
        boolean active
    ) {
    }

    public record TagDto(String id, String name, String slug, boolean active) {
    }

    public record AttachmentDto(
        String id,
        String filename,
        String mediaType,
        long sizeBytes,
        String checksum,
        String downloadUrl,
        Instant uploadedAt
    ) {
    }

    public record NoticeSettingsDto(
        NoticePriority priority,
        boolean requiresAcknowledgement,
        Instant acknowledgementDueAt
    ) {
    }

    public record ContentSummary(
        String id,
        ContentType type,
        String title,
        String summary,
        ContentStatus status,
        CategoryDto category,
        UserSummary owner,
        List<String> tags,
        Instant publishedAt,
        Instant updatedAt,
        boolean acknowledgementRequired
    ) {
    }

    public record ContentDetail(
        String id,
        ContentType type,
        String title,
        String summary,
        ContentStatus status,
        CategoryDto category,
        UserSummary owner,
        List<String> tags,
        Instant publishedAt,
        Instant updatedAt,
        boolean acknowledgementRequired,
        String body,
        int versionNumber,
        List<AttachmentDto> attachments,
        List<String> audiences,
        Instant publishStartAt,
        Instant publishEndAt,
        NoticeSettingsDto notice
    ) {
    }

    public record ContentCreateRequest(
        ContentType type,
        String title,
        String summary,
        String body,
        String categoryId,
        List<String> tagIds,
        List<String> audienceIds,
        NoticeSettingsDto notice
    ) {
    }

    public record ContentUpdateRequest(
        String title,
        String summary,
        String body,
        String categoryId,
        List<String> tagIds,
        List<String> audienceIds,
        String changeNote,
        NoticeSettingsDto notice
    ) {
    }

    public record LifecycleActionRequest(String note) {
    }

    public record PublishRequest(Instant publishStartAt, Instant publishEndAt, String note) {
    }

    public record ContentPage(List<ContentSummary> items, int page, int size, long totalItems, int totalPages) {
    }

    public record SearchResults(
        List<ContentSummary> items,
        int page,
        int size,
        long totalItems,
        int totalPages,
        String query
    ) {
    }

    public record BookmarkCreateRequest(String contentId) {
    }

    public record NoticeRequiredItem(ContentSummary content, boolean acknowledged, Instant acknowledgedAt) {
    }

    public record NoticeAcknowledgementDto(
        String noticeId,
        String userId,
        Instant acknowledgedAt,
        String contentVersionId
    ) {
    }

    public record CategoryWriteRequest(
        String parentId,
        String name,
        String slug,
        String description,
        Integer sortOrder
    ) {
    }

    public record RoleWriteRequest(String code, String name, String description, List<String> permissions) {
    }

    public record AuditLogDto(
        String id,
        UserSummary actor,
        String action,
        String targetType,
        String targetId,
        String outcome,
        Map<String, Object> details,
        Instant occurredAt
    ) {
    }

    public record AuditLogPage(List<AuditLogDto> items, int page, int size, long totalItems) {
    }

    public record AnalyticsSummary(
        long publishedContentCount,
        long draftContentCount,
        long requiredNoticeCount,
        double acknowledgementRate,
        long recentAuditEventCount
    ) {
    }
}
