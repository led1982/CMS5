package com.company.cms.content.api;

import com.company.cms.auth.RoleCode;
import com.company.cms.content.domain.AttachmentStatus;
import com.company.cms.content.domain.ContentStatus;
import com.company.cms.content.domain.ContentType;
import com.company.cms.content.domain.VisibilityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class ContentDtos {
    private ContentDtos() {
    }

    public record DepartmentDto(UUID id, String code, String name) {
    }

    public record UserSummary(UUID id, String displayName, String email, DepartmentDto department, Set<RoleCode> roles) {
    }

    public record CategoryDto(UUID id, String name, String slug, UUID parentId, int sortOrder, boolean active) {
    }

    public record CategoryRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 140) String slug,
        UUID parentId,
        int sortOrder,
        boolean active
    ) {
    }

    public record TagDto(UUID id, String name) {
    }

    public record ContentAudienceDto(UUID id, VisibilityType visibilityType, UUID targetId) {
    }

    public record ContentAudienceRequest(@NotNull VisibilityType visibilityType, UUID targetId) {
    }

    public record AttachmentDto(
        UUID id,
        String originalFilename,
        String contentType,
        long fileSize,
        String storageKey,
        AttachmentStatus status,
        Instant uploadedAt
    ) {
    }

    public record ContentVersionDto(
        UUID id,
        int versionNumber,
        String title,
        String summary,
        String body,
        String changeNote,
        UserSummary createdBy,
        Instant createdAt
    ) {
    }

    public record ContentSummary(
        UUID id,
        ContentType type,
        ContentStatus status,
        String title,
        String summary,
        CategoryDto category,
        List<TagDto> tags,
        UserSummary author,
        boolean pinned,
        boolean requiresAcknowledgement,
        boolean acknowledged,
        Instant publishedAt,
        Instant updatedAt
    ) {
    }

    public record PagedContentSummary(
        List<ContentSummary> items,
        int page,
        int size,
        long totalElements,
        int totalPages
    ) {
    }

    public record ContentDetail(
        UUID id,
        ContentType type,
        ContentStatus status,
        String title,
        String slug,
        String summary,
        String body,
        CategoryDto category,
        List<TagDto> tags,
        DepartmentDto ownerDepartment,
        ContentVersionDto currentVersion,
        List<ContentAudienceDto> audiences,
        List<AttachmentDto> attachments,
        boolean pinned,
        boolean requiresAcknowledgement,
        boolean acknowledged,
        Instant publishedAt,
        Instant publishStartAt,
        Instant publishEndAt,
        Instant updatedAt
    ) {
    }

    public record ContentCreateRequest(
        @NotNull ContentType type,
        @NotBlank @Size(max = 150) String title,
        @Size(max = 500) String summary,
        @NotBlank String body,
        @NotNull UUID categoryId,
        UUID ownerDepartmentId,
        List<String> tags,
        List<ContentAudienceRequest> audiences,
        boolean pinned,
        boolean requiresAcknowledgement,
        Instant publishStartAt,
        Instant publishEndAt,
        String changeNote
    ) {
    }

    public record ContentUpdateRequest(
        @NotNull ContentType type,
        @NotBlank @Size(max = 150) String title,
        @Size(max = 500) String summary,
        @NotBlank String body,
        @NotNull UUID categoryId,
        UUID ownerDepartmentId,
        List<String> tags,
        List<ContentAudienceRequest> audiences,
        boolean pinned,
        boolean requiresAcknowledgement,
        Instant publishStartAt,
        Instant publishEndAt,
        String changeNote
    ) {
    }

    public record ReviewRequest(@NotBlank String decision, @Size(max = 500) String comment) {
    }

    public record PublishRequest(Instant publishStartAt, Instant publishEndAt) {
    }
}
