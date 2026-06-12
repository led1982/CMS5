package com.acme.cms.api;

import com.acme.cms.audit.AuditEvent;
import com.acme.cms.content.model.Attachment;
import com.acme.cms.content.model.Category;
import com.acme.cms.content.model.ContentItem;
import com.acme.cms.content.model.ContentStatus;
import com.acme.cms.content.model.ContentVersion;
import com.acme.cms.content.model.NoticeSettings;
import com.acme.cms.content.model.Tag;
import com.acme.cms.security.model.AudienceGroup;
import com.acme.cms.security.model.RoleEntity;
import com.acme.cms.security.model.UserAccount;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CmsMapper {
    public ApiDtos.RoleSummary toRoleSummary(RoleEntity role) {
        return new ApiDtos.RoleSummary(
            role.getId().toString(),
            role.getCode(),
            role.getName(),
            role.getPermissions().stream().sorted().toList()
        );
    }

    public ApiDtos.UserSummary toUserSummary(UserAccount user) {
        return new ApiDtos.UserSummary(
            user.getId().toString(),
            user.getEmail(),
            user.getDisplayName(),
            user.getDepartment(),
            user.getRoles().stream().sorted(Comparator.comparing(RoleEntity::getCode)).map(this::toRoleSummary).toList(),
            user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .distinct()
                .sorted()
                .toList()
        );
    }

    public ApiDtos.AudienceSummary toAudienceSummary(AudienceGroup audience) {
        return new ApiDtos.AudienceSummary(
            audience.getId().toString(),
            audience.getCode(),
            audience.getName(),
            audience.getType().name(),
            audience.isActive()
        );
    }

    public ApiDtos.CategoryDto toCategory(Category category) {
        return new ApiDtos.CategoryDto(
            category.getId().toString(),
            category.getParentId() == null ? null : category.getParentId().toString(),
            category.getName(),
            category.getSlug(),
            category.getDescription(),
            category.getSortOrder(),
            category.isActive()
        );
    }

    public ApiDtos.TagDto toTag(Tag tag) {
        return new ApiDtos.TagDto(tag.getId().toString(), tag.getName(), tag.getSlug(), tag.isActive());
    }

    public ApiDtos.ContentSummary toContentSummary(ContentItem item, NoticeSettings notice) {
        return new ApiDtos.ContentSummary(
            item.getId().toString(),
            item.getType(),
            item.getTitle(),
            item.getSummary(),
            item.getStatus(),
            toCategory(item.getCategory()),
            toUserSummary(item.getOwner()),
            item.getTags().stream().map(Tag::getName).sorted().toList(),
            item.getStatus() == ContentStatus.PUBLISHED ? item.getPublishStartAt() : null,
            item.getUpdatedAt(),
            notice != null && notice.isRequiresAcknowledgement()
        );
    }

    public ApiDtos.ContentDetail toContentDetail(
        ContentItem item,
        ContentVersion version,
        List<Attachment> attachments,
        NoticeSettings notice
    ) {
        var summary = toContentSummary(item, notice);
        return new ApiDtos.ContentDetail(
            summary.id(),
            summary.type(),
            summary.title(),
            summary.summary(),
            summary.status(),
            summary.category(),
            summary.owner(),
            summary.tags(),
            summary.publishedAt(),
            summary.updatedAt(),
            summary.acknowledgementRequired(),
            version == null ? "" : version.getBody(),
            version == null ? 0 : version.getVersionNumber(),
            attachments.stream().map(this::toAttachment).toList(),
            item.getAudiences().stream().map(AudienceGroup::getName).sorted().toList(),
            item.getPublishStartAt(),
            item.getPublishEndAt(),
            notice == null ? null : toNotice(notice)
        );
    }

    public ApiDtos.AttachmentDto toAttachment(Attachment attachment) {
        return new ApiDtos.AttachmentDto(
            attachment.getId().toString(),
            attachment.getFilename(),
            attachment.getMediaType(),
            attachment.getSizeBytes(),
            attachment.getChecksum(),
            "/api/v1/attachments/" + attachment.getId() + "/download",
            attachment.getUploadedAt()
        );
    }

    public ApiDtos.NoticeSettingsDto toNotice(NoticeSettings notice) {
        return new ApiDtos.NoticeSettingsDto(
            notice.getPriority(),
            notice.isRequiresAcknowledgement(),
            notice.getAcknowledgementDueAt()
        );
    }

    public ApiDtos.AuditLogDto toAuditLog(AuditEvent auditEvent) {
        return new ApiDtos.AuditLogDto(
            auditEvent.getId().toString(),
            auditEvent.getActor() == null ? null : toUserSummary(auditEvent.getActor()),
            auditEvent.getAction(),
            auditEvent.getTargetType(),
            auditEvent.getTargetId(),
            auditEvent.getOutcome().name(),
            auditEvent.getDetails() == null ? Map.of() : Map.of("summary", auditEvent.getDetails()),
            auditEvent.getOccurredAt()
        );
    }
}
