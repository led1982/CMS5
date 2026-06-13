package com.company.cms.content.service;

import com.company.cms.announcement.service.AcknowledgementTargetService;
import com.company.cms.audit.AuditAction;
import com.company.cms.auth.AuthenticatedUser;
import com.company.cms.auth.CurrentUserProvider;
import com.company.cms.auth.Department;
import com.company.cms.auth.DepartmentRepository;
import com.company.cms.auth.UserAccount;
import com.company.cms.auth.UserAccountRepository;
import com.company.cms.common.api.ApiException;
import com.company.cms.common.observability.CmsObservabilityService;
import com.company.cms.content.api.ContentDtos.ContentAudienceRequest;
import com.company.cms.content.api.ContentDtos.ContentCreateRequest;
import com.company.cms.content.api.ContentDtos.ContentUpdateRequest;
import com.company.cms.content.api.ContentDtos.PublishRequest;
import com.company.cms.content.api.ContentDtos.ReviewRequest;
import com.company.cms.content.domain.Category;
import com.company.cms.content.domain.ContentAudience;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.domain.ContentStatus;
import com.company.cms.content.domain.ContentType;
import com.company.cms.content.domain.ContentVersion;
import com.company.cms.content.domain.Tag;
import com.company.cms.content.repository.CategoryRepository;
import com.company.cms.content.repository.ContentAudienceRepository;
import com.company.cms.content.repository.ContentItemRepository;
import com.company.cms.content.repository.ContentVersionRepository;
import com.company.cms.content.repository.TagRepository;
import jakarta.persistence.criteria.Predicate;
import java.text.Normalizer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContentWorkflowService {
    private final ContentItemRepository contentItemRepository;
    private final ContentVersionRepository contentVersionRepository;
    private final ContentAudienceRepository contentAudienceRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserAccountRepository userAccountRepository;
    private final DepartmentRepository departmentRepository;
    private final CurrentUserProvider currentUserProvider;
    private final ContentValidationService validationService;
    private final ContentAuditPublisher auditPublisher;
    private final ObjectProvider<AcknowledgementTargetService> acknowledgementTargetService;
    private final CmsObservabilityService observabilityService;

    public ContentWorkflowService(ContentItemRepository contentItemRepository,
            ContentVersionRepository contentVersionRepository,
            ContentAudienceRepository contentAudienceRepository,
            CategoryRepository categoryRepository,
            TagRepository tagRepository,
            UserAccountRepository userAccountRepository,
            DepartmentRepository departmentRepository,
            CurrentUserProvider currentUserProvider,
            ContentValidationService validationService,
            ContentAuditPublisher auditPublisher,
            ObjectProvider<AcknowledgementTargetService> acknowledgementTargetService,
            CmsObservabilityService observabilityService) {
        this.contentItemRepository = contentItemRepository;
        this.contentVersionRepository = contentVersionRepository;
        this.contentAudienceRepository = contentAudienceRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.userAccountRepository = userAccountRepository;
        this.departmentRepository = departmentRepository;
        this.currentUserProvider = currentUserProvider;
        this.validationService = validationService;
        this.auditPublisher = auditPublisher;
        this.acknowledgementTargetService = acknowledgementTargetService;
        this.observabilityService = observabilityService;
    }

    @Transactional(readOnly = true)
    public Page<ContentItem> list(ContentStatus status, ContentType type, UUID ownerDepartmentId, int page, int size) {
        Specification<ContentItem> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (type != null) {
                predicates.add(builder.equal(root.get("type"), type));
            }
            if (ownerDepartmentId != null) {
                predicates.add(builder.equal(root.get("ownerDepartment").get("id"), ownerDepartmentId));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
        return contentItemRepository.findAll(
            specification,
            PageRequest.of(page, Math.min(Math.max(size, 1), 100), Sort.by(Sort.Direction.DESC, "updatedAt"))
        );
    }

    @Transactional
    public ContentItem create(ContentCreateRequest request) {
        AuthenticatedUser actor = currentUserProvider.currentUser();
        validateRequestFlags(request.type(), request.requiresAcknowledgement());
        UserAccount author = user(actor.id());
        Category category = category(request.categoryId());
        Department ownerDepartment = department(request.ownerDepartmentId());
        Set<Tag> tags = tags(request.tags());
        ContentItem content = new ContentItem(
            request.type(),
            request.title().trim(),
            uniqueSlug(request.title()),
            request.summary(),
            category,
            ownerDepartment,
            author
        );
        content.updateDraft(request.type(), request.title().trim(), content.getSlug(), request.summary(), category,
            ownerDepartment, request.pinned(), request.requiresAcknowledgement(), tags);
        content = contentItemRepository.save(content);
        ContentVersion version = saveVersion(content, 1, request.title(), request.summary(), request.body(),
            request.changeNote(), author);
        content.setCurrentVersionId(version.getId());
        contentItemRepository.save(content);
        replaceAudiences(content, request.audiences());
        auditPublisher.publish(actor, AuditAction.CREATE, content, "Content draft created.");
        observabilityService.workflowEvent("CREATE", content.getId().toString());
        return content;
    }

    @Transactional
    public ContentItem update(UUID contentId, ContentUpdateRequest request) {
        AuthenticatedUser actor = currentUserProvider.currentUser();
        validateRequestFlags(request.type(), request.requiresAcknowledgement());
        ContentItem content = content(contentId);
        if (content.getStatus() == ContentStatus.ARCHIVED) {
            throw ApiException.conflict("ARCHIVED_CONTENT", "Archived content cannot be edited.");
        }
        UserAccount editor = user(actor.id());
        Category category = category(request.categoryId());
        Department ownerDepartment = department(request.ownerDepartmentId());
        Set<Tag> tags = tags(request.tags());
        content.updateDraft(request.type(), request.title().trim(), uniqueSlug(request.title()), request.summary(),
            category, ownerDepartment, request.pinned(), request.requiresAcknowledgement(), tags);
        content.setStatus(ContentStatus.DRAFT);
        int nextVersion = contentVersionRepository.findTopByContentOrderByVersionNumberDesc(content)
            .map(ContentVersion::getVersionNumber)
            .orElse(0) + 1;
        ContentVersion version = saveVersion(content, nextVersion, request.title(), request.summary(), request.body(),
            request.changeNote(), editor);
        content.setCurrentVersionId(version.getId());
        replaceAudiences(content, request.audiences());
        auditPublisher.publish(actor, AuditAction.UPDATE, content, "Content draft updated.");
        observabilityService.workflowEvent("UPDATE", content.getId().toString());
        return contentItemRepository.save(content);
    }

    @Transactional
    public ContentItem submit(UUID contentId, String changeNote) {
        AuthenticatedUser actor = currentUserProvider.currentUser();
        ContentItem content = content(contentId);
        if (content.getStatus() != ContentStatus.DRAFT) {
            throw ApiException.conflict("INVALID_STATUS", "Only draft content can be submitted for review.");
        }
        ContentVersion version = currentVersion(content);
        List<ContentAudience> audiences = contentAudienceRepository.findByContent(content);
        validationService.validateForReview(content, version.getBody(), audiences);
        content.setStatus(ContentStatus.IN_REVIEW);
        auditPublisher.publish(actor, AuditAction.SUBMIT_REVIEW, content, emptyIfNull(changeNote));
        observabilityService.workflowEvent("SUBMIT_REVIEW", content.getId().toString());
        return contentItemRepository.save(content);
    }

    @Transactional
    public ContentItem review(UUID contentId, ReviewRequest request) {
        AuthenticatedUser actor = currentUserProvider.currentUser();
        ContentItem content = content(contentId);
        if (content.getStatus() != ContentStatus.IN_REVIEW) {
            throw ApiException.conflict("INVALID_STATUS", "Only content in review can be reviewed.");
        }
        content.assignReviewer(user(actor.id()));
        if ("REJECT".equalsIgnoreCase(request.decision())) {
            content.setStatus(ContentStatus.DRAFT);
            auditPublisher.publish(actor, AuditAction.REJECT, content, emptyIfNull(request.comment()));
            observabilityService.workflowEvent("REJECT", content.getId().toString());
            return contentItemRepository.save(content);
        }
        if (!"APPROVE".equalsIgnoreCase(request.decision())) {
            throw ApiException.badRequest("INVALID_REVIEW_DECISION", "Review decision must be APPROVE or REJECT.");
        }
        auditPublisher.publish(actor, AuditAction.APPROVE, content, emptyIfNull(request.comment()));
        observabilityService.workflowEvent("APPROVE", content.getId().toString());
        return contentItemRepository.save(content);
    }

    @Transactional
    public ContentItem publish(UUID contentId, PublishRequest request) {
        AuthenticatedUser actor = currentUserProvider.currentUser();
        ContentItem content = content(contentId);
        if (content.getStatus() != ContentStatus.IN_REVIEW && content.getStatus() != ContentStatus.PUBLISHED) {
            throw ApiException.conflict("INVALID_STATUS", "Only reviewed content can be published.");
        }
        validationService.validatePublishWindow(request.publishStartAt(), request.publishEndAt());
        content.publish(Instant.now(), request.publishStartAt(), request.publishEndAt());
        content = contentItemRepository.save(content);
        if (content.getType() == ContentType.ANNOUNCEMENT && content.isRequiresAcknowledgement()) {
            acknowledgementTargetService.ifAvailable(service -> service.generateTargets(content));
        }
        auditPublisher.publish(actor, AuditAction.PUBLISH, content, "Content published.");
        observabilityService.workflowEvent("PUBLISH", content.getId().toString());
        return content;
    }

    @Transactional
    public ContentItem archive(UUID contentId, String reason) {
        AuthenticatedUser actor = currentUserProvider.currentUser();
        ContentItem content = content(contentId);
        content.setStatus(ContentStatus.ARCHIVED);
        auditPublisher.publish(actor, AuditAction.ARCHIVE, content, emptyIfNull(reason));
        observabilityService.workflowEvent("ARCHIVE", content.getId().toString());
        return contentItemRepository.save(content);
    }

    @Transactional(readOnly = true)
    public ContentItem content(UUID contentId) {
        return contentItemRepository.findWithCategoryById(contentId)
            .orElseThrow(() -> ApiException.notFound("CONTENT_NOT_FOUND", "Content was not found."));
    }

    @Transactional(readOnly = true)
    public ContentVersion currentVersion(ContentItem content) {
        if (content.getCurrentVersionId() == null) {
            return contentVersionRepository.findTopByContentOrderByVersionNumberDesc(content)
                .orElseThrow(() -> ApiException.notFound("VERSION_NOT_FOUND", "Content version was not found."));
        }
        return contentVersionRepository.findById(content.getCurrentVersionId())
            .orElseThrow(() -> ApiException.notFound("VERSION_NOT_FOUND", "Content version was not found."));
    }

    @Transactional(readOnly = true)
    public List<ContentVersion> versions(UUID contentId) {
        return contentVersionRepository.findByContentOrderByVersionNumberDesc(content(contentId));
    }

    private ContentVersion saveVersion(ContentItem content, int number, String title, String summary, String body,
            String changeNote, UserAccount user) {
        return contentVersionRepository.save(new ContentVersion(content, number, title.trim(), summary, body,
            emptyIfNull(changeNote), user));
    }

    private void replaceAudiences(ContentItem content, List<ContentAudienceRequest> requests) {
        contentAudienceRepository.deleteByContent(content);
        List<ContentAudience> audiences = (requests == null || requests.isEmpty())
            ? List.of(new ContentAudience(content, com.company.cms.content.domain.VisibilityType.ALL_EMPLOYEES, null))
            : requests.stream()
                .map(request -> new ContentAudience(content, request.visibilityType(), request.targetId()))
                .toList();
        contentAudienceRepository.saveAll(audiences);
    }

    private Set<Tag> tags(List<String> names) {
        Set<Tag> tags = new LinkedHashSet<>();
        if (names == null) {
            return tags;
        }
        for (String name : names) {
            if (name == null || name.trim().isEmpty()) {
                continue;
            }
            String normalized = Tag.normalize(name);
            tags.add(tagRepository.findByNormalizedName(normalized).orElseGet(() -> tagRepository.save(new Tag(name))));
        }
        return tags;
    }

    private Category category(UUID id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> ApiException.badRequest("CATEGORY_NOT_FOUND", "Category was not found."));
    }

    private Department department(UUID id) {
        if (id == null) {
            return null;
        }
        return departmentRepository.findById(id)
            .orElseThrow(() -> ApiException.badRequest("DEPARTMENT_NOT_FOUND", "Department was not found."));
    }

    private UserAccount user(UUID id) {
        return userAccountRepository.findDetailedById(id)
            .orElseThrow(() -> ApiException.unauthorized("USER_NOT_FOUND", "Authenticated user was not found."));
    }

    private String uniqueSlug(String title) {
        String normalized = Normalizer.normalize(title, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .replaceAll("[^a-zA-Z0-9]+", "-")
            .replaceAll("(^-|-$)", "")
            .toLowerCase(Locale.ROOT);
        if (normalized.isBlank()) {
            normalized = "content";
        }
        return normalized + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String emptyIfNull(String value) {
        return value == null ? "" : value;
    }

    private void validateRequestFlags(ContentType type, boolean requiresAcknowledgement) {
        if (requiresAcknowledgement && type != ContentType.ANNOUNCEMENT) {
            throw ApiException.badRequest("ACK_ONLY_FOR_ANNOUNCEMENT", "Acknowledgement is only available for announcements.");
        }
    }
}
