package com.acme.cms.content;

import com.acme.cms.api.ApiDtos;
import com.acme.cms.api.ApiException;
import com.acme.cms.audit.AuditEventService;
import com.acme.cms.content.model.Category;
import com.acme.cms.content.model.ContentItem;
import com.acme.cms.content.model.ContentStatus;
import com.acme.cms.content.model.ContentType;
import com.acme.cms.content.model.ContentVersion;
import com.acme.cms.content.model.ContentVisibility;
import com.acme.cms.content.model.NoticeSettings;
import com.acme.cms.content.model.Tag;
import com.acme.cms.content.repository.CategoryRepository;
import com.acme.cms.content.repository.ContentItemRepository;
import com.acme.cms.content.repository.ContentVersionRepository;
import com.acme.cms.content.repository.NoticeSettingsRepository;
import com.acme.cms.content.repository.TagRepository;
import com.acme.cms.security.CurrentUser;
import com.acme.cms.security.model.AudienceGroup;
import com.acme.cms.security.model.UserAccount;
import com.acme.cms.security.repository.AudienceGroupRepository;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContentLifecycleService {
    private final ContentItemRepository contentItems;
    private final ContentVersionRepository versions;
    private final CategoryRepository categories;
    private final TagRepository tags;
    private final AudienceGroupRepository audiences;
    private final NoticeSettingsRepository noticeSettings;
    private final CurrentUser currentUser;
    private final AuditEventService audit;

    public ContentLifecycleService(
        ContentItemRepository contentItems,
        ContentVersionRepository versions,
        CategoryRepository categories,
        TagRepository tags,
        AudienceGroupRepository audiences,
        NoticeSettingsRepository noticeSettings,
        CurrentUser currentUser,
        AuditEventService audit
    ) {
        this.contentItems = contentItems;
        this.versions = versions;
        this.categories = categories;
        this.tags = tags;
        this.audiences = audiences;
        this.noticeSettings = noticeSettings;
        this.currentUser = currentUser;
        this.audit = audit;
    }

    @Transactional
    public ContentItem create(ApiDtos.ContentCreateRequest request) {
        UserAccount actor = currentUser.get();
        require(actor, "CONTENT_WRITE");
        validateRequired(request.title(), "title");
        validateRequired(request.summary(), "summary");
        validateRequired(request.body(), "body");
        if (request.type() == null) {
            throw badRequest("type is required.");
        }

        Category category = categories.findById(parseId(request.categoryId(), "categoryId"))
            .filter(Category::isActive)
            .orElseThrow(() -> notFound("Category was not found or is inactive."));

        ContentItem item = new ContentItem();
        item.setType(request.type());
        item.setTitle(request.title().trim());
        item.setSummary(request.summary().trim());
        item.setOwner(actor);
        item.setCategory(category);
        item.setStatus(ContentStatus.DRAFT);
        applyTags(item, request.tagIds());
        applyAudiences(item, request.audienceIds());
        item = contentItems.save(item);

        ContentVersion version = createVersion(item, actor, request.title(), request.summary(), request.body(), "Initial draft");
        item.setCurrentVersionId(version.getId());
        item = contentItems.save(item);
        saveNoticeSettings(item, request.notice());
        audit.record(actor, "CONTENT_CREATED", "CONTENT", item.getId().toString(), item.getTitle());
        return item;
    }

    @Transactional
    public ContentItem update(UUID contentId, ApiDtos.ContentUpdateRequest request) {
        UserAccount actor = currentUser.get();
        require(actor, "CONTENT_WRITE");
        ContentItem item = findForManagement(contentId);
        if (item.getStatus() == ContentStatus.ARCHIVED) {
            throw conflict("Archived content is read-only.");
        }

        String title = valueOrCurrent(request.title(), item.getTitle());
        String summary = valueOrCurrent(request.summary(), item.getSummary());
        validateRequired(title, "title");
        validateRequired(summary, "summary");

        item.setTitle(title.trim());
        item.setSummary(summary.trim());
        if (request.categoryId() != null) {
            item.setCategory(categories.findById(parseId(request.categoryId(), "categoryId"))
                .orElseThrow(() -> notFound("Category was not found.")));
        }
        if (request.tagIds() != null) {
            applyTags(item, request.tagIds());
        }
        if (request.audienceIds() != null) {
            applyAudiences(item, request.audienceIds());
        }
        ContentVersion latest = latestVersion(item);
        if (request.body() != null || request.title() != null || request.summary() != null) {
            ContentVersion version = createVersion(
                item,
                actor,
                title,
                summary,
                request.body() == null ? latest.getBody() : request.body(),
                request.changeNote() == null ? "Updated content" : request.changeNote()
            );
            item.setCurrentVersionId(version.getId());
            if (item.getStatus() == ContentStatus.APPROVED || item.getStatus() == ContentStatus.PUBLISHED || item.getStatus() == ContentStatus.SCHEDULED) {
                item.setStatus(ContentStatus.DRAFT);
            }
        }
        item = contentItems.save(item);
        saveNoticeSettings(item, request.notice());
        audit.record(actor, "CONTENT_UPDATED", "CONTENT", item.getId().toString(), item.getTitle());
        return item;
    }

    @Transactional
    public ContentItem submit(UUID contentId) {
        UserAccount actor = currentUser.get();
        require(actor, "CONTENT_WRITE");
        ContentItem item = findForManagement(contentId);
        if (item.getStatus() != ContentStatus.DRAFT && item.getStatus() != ContentStatus.REJECTED) {
            throw conflict("Only draft or rejected content can be submitted.");
        }
        validatePublishableMetadata(item);
        item.setStatus(ContentStatus.SUBMITTED);
        audit.record(actor, "CONTENT_SUBMITTED", "CONTENT", item.getId().toString(), item.getTitle());
        return item;
    }

    @Transactional
    public ContentItem approve(UUID contentId, String note) {
        UserAccount actor = currentUser.get();
        require(actor, "CONTENT_REVIEW");
        ContentItem item = findForManagement(contentId);
        if (item.getStatus() != ContentStatus.SUBMITTED) {
            throw conflict("Only submitted content can be approved.");
        }
        ContentVersion version = latestVersion(item);
        version.setApprovedBy(actor);
        version.setApprovedAt(Instant.now());
        versions.save(version);
        item.setCurrentVersionId(version.getId());
        item.setStatus(ContentStatus.APPROVED);
        audit.record(actor, "CONTENT_APPROVED", "CONTENT", item.getId().toString(), note == null ? item.getTitle() : note);
        return item;
    }

    @Transactional
    public ContentItem publish(UUID contentId, ApiDtos.PublishRequest request) {
        UserAccount actor = currentUser.get();
        require(actor, "CONTENT_PUBLISH");
        ContentItem item = findForManagement(contentId);
        if (item.getStatus() != ContentStatus.APPROVED && item.getStatus() != ContentStatus.SCHEDULED) {
            throw conflict("Only approved or scheduled content can be published.");
        }
        validatePublishableMetadata(item);
        ContentVersion version = latestVersion(item);
        if (version.getApprovedAt() == null) {
            throw conflict("The current version must be approved before publishing.");
        }

        Instant start = request == null || request.publishStartAt() == null ? Instant.now() : request.publishStartAt();
        Instant end = request == null ? null : request.publishEndAt();
        if (end != null && !end.isAfter(start)) {
            throw badRequest("publishEndAt must be after publishStartAt.");
        }
        item.setPublishStartAt(start);
        item.setPublishEndAt(end);
        item.setStatus(start.isAfter(Instant.now()) ? ContentStatus.SCHEDULED : ContentStatus.PUBLISHED);
        audit.record(actor, "CONTENT_PUBLISHED", "CONTENT", item.getId().toString(), item.getTitle());
        return item;
    }

    @Transactional
    public ContentItem archive(UUID contentId, String note) {
        UserAccount actor = currentUser.get();
        require(actor, "CONTENT_WRITE");
        ContentItem item = findForManagement(contentId);
        if (item.getStatus() == ContentStatus.ARCHIVED) {
            return item;
        }
        item.setStatus(ContentStatus.ARCHIVED);
        item.setArchivedAt(Instant.now());
        audit.record(actor, "CONTENT_ARCHIVED", "CONTENT", item.getId().toString(), note == null ? item.getTitle() : note);
        return item;
    }

    @Transactional(readOnly = true)
    public ContentItem getVisibleOrManageable(UUID contentId) {
        UserAccount actor = currentUser.get();
        ContentItem item = contentItems.findById(contentId).orElseThrow(() -> notFound("Content was not found."));
        if (canManage(actor) || canView(actor, item)) {
            return item;
        }
        throw notFound("Content was not found.");
    }

    @Transactional(readOnly = true)
    public ContentItem getPortalDetail(UUID contentId) {
        UserAccount actor = currentUser.get();
        ContentItem item = contentItems.findById(contentId).orElseThrow(() -> notFound("Content was not found."));
        if (!canView(actor, item)) {
            throw notFound("Content was not found.");
        }
        return item;
    }

    @Transactional(readOnly = true)
    public List<ContentItem> listForCurrentUser(ContentStatus status, ContentType type, String ownerUserId) {
        UserAccount actor = currentUser.get();
        return contentItems.findAll().stream()
            .filter(item -> canManage(actor) || canView(actor, item))
            .filter(item -> status == null || item.getStatus() == status)
            .filter(item -> type == null || item.getType() == type)
            .filter(item -> ownerUserId == null || item.getOwner().getId().toString().equals(ownerUserId))
            .sorted(Comparator.comparing(ContentItem::getUpdatedAt).reversed())
            .toList();
    }

    @Transactional(readOnly = true)
    public List<ContentItem> portalFeed(ContentType type, String categoryId) {
        UserAccount actor = currentUser.get();
        return contentItems.findAll().stream()
            .filter(item -> canView(actor, item))
            .filter(item -> type == null || item.getType() == type)
            .filter(item -> categoryId == null || item.getCategory().getId().toString().equals(categoryId))
            .sorted(Comparator.comparing(ContentItem::getUpdatedAt).reversed())
            .toList();
    }

    public ContentVersion latestVersion(ContentItem item) {
        if (item.getCurrentVersionId() != null) {
            return versions.findById(item.getCurrentVersionId()).orElseGet(() -> fallbackLatest(item));
        }
        return fallbackLatest(item);
    }

    public boolean canViewCurrentUser(ContentItem item) {
        return canView(currentUser.get(), item);
    }

    public boolean canView(UserAccount user, ContentItem item) {
        if (item.getStatus() != ContentStatus.PUBLISHED) {
            return false;
        }
        Instant now = Instant.now();
        if (item.getPublishStartAt() != null && item.getPublishStartAt().isAfter(now)) {
            return false;
        }
        if (item.getPublishEndAt() != null && !item.getPublishEndAt().isAfter(now)) {
            return false;
        }
        if (item.getVisibility() == ContentVisibility.ALL_EMPLOYEES) {
            return currentUser.hasPermission(user, "PORTAL_READ");
        }
        Set<UUID> userAudienceIds = user.getAudiences().stream().map(AudienceGroup::getId).collect(java.util.stream.Collectors.toSet());
        return item.getAudiences().stream().anyMatch(audience -> userAudienceIds.contains(audience.getId()));
    }

    public NoticeSettings noticeFor(ContentItem item) {
        return noticeSettings.findById(item.getId()).orElse(null);
    }

    private ContentItem findForManagement(UUID contentId) {
        UserAccount actor = currentUser.get();
        if (!canManage(actor)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Content management permission is required.");
        }
        return contentItems.findById(contentId).orElseThrow(() -> notFound("Content was not found."));
    }

    private boolean canManage(UserAccount user) {
        return currentUser.hasPermission(user, "CONTENT_WRITE")
            || currentUser.hasPermission(user, "CONTENT_REVIEW")
            || currentUser.hasPermission(user, "CONTENT_PUBLISH")
            || currentUser.hasPermission(user, "ADMIN_ACCESS");
    }

    private void require(UserAccount user, String permission) {
        if (!currentUser.hasPermission(user, permission)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Permission required: " + permission);
        }
    }

    private void validatePublishableMetadata(ContentItem item) {
        validateRequired(item.getTitle(), "title");
        validateRequired(item.getSummary(), "summary");
        if (!item.getCategory().isActive()) {
            throw badRequest("Content category is inactive.");
        }
        if (item.getVisibility() == ContentVisibility.SELECTED_AUDIENCES && item.getAudiences().isEmpty()) {
            throw badRequest("At least one active audience is required before publishing.");
        }
        latestVersion(item);
    }

    private ContentVersion createVersion(ContentItem item, UserAccount actor, String title, String summary, String body, String changeNote) {
        validateRequired(body, "body");
        ContentVersion latest = versions.findTopByContentItemOrderByVersionNumberDesc(item).orElse(null);
        ContentVersion version = new ContentVersion();
        version.setContentItem(item);
        version.setVersionNumber(latest == null ? 1 : latest.getVersionNumber() + 1);
        version.setTitle(title.trim());
        version.setSummary(summary.trim());
        version.setBody(body);
        version.setChangeNote(changeNote);
        version.setCreatedBy(actor);
        return versions.save(version);
    }

    private ContentVersion latestVersionFallbackRequired(ContentItem item) {
        return versions.findTopByContentItemOrderByVersionNumberDesc(item)
            .orElseThrow(() -> conflict("Content has no version."));
    }

    private ContentVersion fallbackLatest(ContentItem item) {
        return latestVersionFallbackRequired(item);
    }

    private void saveNoticeSettings(ContentItem item, ApiDtos.NoticeSettingsDto dto) {
        if (item.getType() != ContentType.NOTICE) {
            noticeSettings.findById(item.getId()).ifPresent(noticeSettings::delete);
            return;
        }
        NoticeSettings settings = noticeSettings.findById(item.getId()).orElseGet(NoticeSettings::new);
        settings.setContentItem(item);
        settings.setPriority(dto == null || dto.priority() == null ? com.acme.cms.content.model.NoticePriority.NORMAL : dto.priority());
        settings.setRequiresAcknowledgement(dto != null && dto.requiresAcknowledgement());
        settings.setAcknowledgementDueAt(dto == null ? null : dto.acknowledgementDueAt());
        noticeSettings.save(settings);
    }

    private void applyTags(ContentItem item, List<String> tagIds) {
        item.getTags().clear();
        if (tagIds == null) {
            return;
        }
        Set<Tag> resolved = new LinkedHashSet<>();
        for (String id : tagIds) {
            resolved.add(tags.findById(parseId(id, "tagIds")).orElseThrow(() -> notFound("Tag was not found.")));
        }
        item.setTags(resolved);
    }

    private void applyAudiences(ContentItem item, List<String> audienceIds) {
        item.getAudiences().clear();
        if (audienceIds == null || audienceIds.isEmpty()) {
            item.setVisibility(ContentVisibility.ALL_EMPLOYEES);
            return;
        }
        Set<AudienceGroup> resolved = new LinkedHashSet<>();
        for (String id : audienceIds) {
            resolved.add(audiences.findById(parseId(id, "audienceIds"))
                .filter(AudienceGroup::isActive)
                .orElseThrow(() -> notFound("Audience was not found or is inactive.")));
        }
        item.setVisibility(ContentVisibility.SELECTED_AUDIENCES);
        item.setAudiences(resolved);
    }

    private UUID parseId(String value, String field) {
        if (value == null || value.isBlank()) {
            throw badRequest(field + " is required.");
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw badRequest(field + " must be a UUID.");
        }
    }

    private void validateRequired(String value, String field) {
        if (value == null || value.isBlank()) {
            throw badRequest(field + " is required.");
        }
    }

    private String valueOrCurrent(String value, String current) {
        return value == null ? current : value;
    }

    private ApiException badRequest(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", message);
    }

    private ApiException conflict(String message) {
        return new ApiException(HttpStatus.CONFLICT, "INVALID_STATE", message);
    }

    private ApiException notFound(String message) {
        return new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", message);
    }
}
