package com.company.cms.content.api;

import com.company.cms.auth.Department;
import com.company.cms.auth.RoleEntity;
import com.company.cms.auth.UserAccount;
import com.company.cms.content.api.ContentDtos.AttachmentDto;
import com.company.cms.content.api.ContentDtos.CategoryDto;
import com.company.cms.content.api.ContentDtos.ContentAudienceDto;
import com.company.cms.content.api.ContentDtos.ContentDetail;
import com.company.cms.content.api.ContentDtos.ContentSummary;
import com.company.cms.content.api.ContentDtos.ContentVersionDto;
import com.company.cms.content.api.ContentDtos.DepartmentDto;
import com.company.cms.content.api.ContentDtos.TagDto;
import com.company.cms.content.api.ContentDtos.UserSummary;
import com.company.cms.content.domain.Attachment;
import com.company.cms.content.domain.Category;
import com.company.cms.content.domain.ContentAudience;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.domain.ContentVersion;
import com.company.cms.content.domain.Tag;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ContentMapper {
    public ContentSummary summary(ContentItem item, boolean acknowledged) {
        return new ContentSummary(
            item.getId(),
            item.getType(),
            item.getStatus(),
            item.getTitle(),
            item.getSummary(),
            category(item.getCategory()),
            tags(item.getTags().stream().toList()),
            user(item.getAuthor()),
            item.isPinned(),
            item.isRequiresAcknowledgement(),
            acknowledged,
            item.getPublishedAt(),
            item.getUpdatedAt()
        );
    }

    public ContentDetail detail(ContentItem item, ContentVersion version, List<ContentAudience> audiences,
            List<Attachment> attachments, boolean acknowledged) {
        return new ContentDetail(
            item.getId(),
            item.getType(),
            item.getStatus(),
            item.getTitle(),
            item.getSlug(),
            item.getSummary(),
            version == null ? "" : version.getBody(),
            category(item.getCategory()),
            tags(item.getTags().stream().toList()),
            department(item.getOwnerDepartment()),
            version(version),
            audiences.stream().map(this::audience).toList(),
            attachments.stream().map(this::attachment).toList(),
            item.isPinned(),
            item.isRequiresAcknowledgement(),
            acknowledged,
            item.getPublishedAt(),
            item.getPublishStartAt(),
            item.getPublishEndAt(),
            item.getUpdatedAt()
        );
    }

    public CategoryDto category(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryDto(
            category.getId(),
            category.getName(),
            category.getSlug(),
            category.getParent() == null ? null : category.getParent().getId(),
            category.getSortOrder(),
            category.isActive()
        );
    }

    public TagDto tag(Tag tag) {
        return new TagDto(tag.getId(), tag.getName());
    }

    public List<TagDto> tags(List<Tag> tags) {
        return tags.stream()
            .sorted(Comparator.comparing(Tag::getName))
            .map(this::tag)
            .toList();
    }

    public ContentAudienceDto audience(ContentAudience audience) {
        return new ContentAudienceDto(audience.getId(), audience.getVisibilityType(), audience.getTargetId());
    }

    public AttachmentDto attachment(Attachment attachment) {
        return new AttachmentDto(
            attachment.getId(),
            attachment.getOriginalFilename(),
            attachment.getContentType(),
            attachment.getFileSize(),
            attachment.getStorageKey(),
            attachment.getStatus(),
            attachment.getUploadedAt()
        );
    }

    public ContentVersionDto version(ContentVersion version) {
        if (version == null) {
            return null;
        }
        return new ContentVersionDto(
            version.getId(),
            version.getVersionNumber(),
            version.getTitle(),
            version.getSummary(),
            version.getBody(),
            version.getChangeNote(),
            user(version.getCreatedBy()),
            version.getCreatedAt()
        );
    }

    public DepartmentDto department(Department department) {
        if (department == null) {
            return null;
        }
        return new DepartmentDto(department.getId(), department.getCode(), department.getName());
    }

    public UserSummary user(UserAccount user) {
        return new UserSummary(
            user.getId(),
            user.getDisplayName(),
            user.getEmail(),
            department(user.getDepartment()),
            user.getRoles().stream().map(RoleEntity::getCode).collect(java.util.stream.Collectors.toSet())
        );
    }
}
