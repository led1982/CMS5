package com.company.cms.content.service;

import java.text.Normalizer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.company.cms.audit.AuditService;
import com.company.cms.auth.AuthUser;
import com.company.cms.content.domain.Attachment;
import com.company.cms.content.domain.ContentEnums.AttachmentScanStatus;
import com.company.cms.content.domain.ContentEnums.ContentStatus;
import com.company.cms.content.domain.ContentEnums.ContentType;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.domain.ContentVersion;
import com.company.cms.content.repository.ContentItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ContentService {
    private static final UUID CATEGORY_SECURITY = UUID.fromString("10000000-0000-0000-0000-000000000001");
    private static final UUID CATEGORY_ENGINEERING = UUID.fromString("10000000-0000-0000-0000-000000000002");
    private static final UUID CATEGORY_HR = UUID.fromString("10000000-0000-0000-0000-000000000003");

    private static final Map<UUID, String> CATEGORY_NAMES = Map.of(
            CATEGORY_SECURITY, "Security",
            CATEGORY_ENGINEERING, "Engineering",
            CATEGORY_HR, "HR"
    );

    private final ContentItemRepository repository;
    private final AuditService auditService;
    private final ConcurrentMap<UUID, List<ContentVersion>> versions = new ConcurrentHashMap<>();

    public ContentService(ContentItemRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    @PostConstruct
    void seed() {
        if (!repository.isEmpty()) {
            return;
        }
        ContentItem security = newItem(
                ContentType.ARTICLE,
                ContentStatus.PUBLISHED,
                "계정 보안 기준 개정 안내",
                "다중 인증, 비밀번호 재사용 제한, 관리자 계정 점검 기준입니다.",
                "## 주요 변경\n\n관리자 계정은 MFA를 필수로 사용합니다.",
                CATEGORY_SECURITY,
                "Security Ops",
                true,
                false
        );
        security.setPublishedAt(Instant.now().minusSeconds(86_400));
        security.getTags().addAll(List.of("security", "account"));
        security.getAudiences().add("ALL_EMPLOYEES");
        security.getAttachments().add(new Attachment("security-checklist.pdf", "application/pdf", 864000, AttachmentScanStatus.CLEAN));
        repository.save(security);
        recordVersion(security, "Initial published version", security.getAuthorId());

        ContentItem runbook = newItem(
                ContentType.DOCUMENT,
                ContentStatus.PUBLISHED,
                "서비스 릴리스 런북",
                "정기 배포 전후 확인 항목과 롤백 기준입니다.",
                "## 릴리스 체크포인트\n\n릴리스 오너는 배포 24시간 전 변경 범위를 등록합니다.",
                CATEGORY_ENGINEERING,
                "Platform Team",
                false,
                false
        );
        runbook.setPublishedAt(Instant.now().minusSeconds(172_800));
        runbook.getTags().addAll(List.of("release", "runbook"));
        runbook.getAudiences().add("ROLE:EMPLOYEE");
        repository.save(runbook);
        recordVersion(runbook, "Initial published version", runbook.getAuthorId());

        ContentItem notice = newItem(
                ContentType.NOTICE,
                ContentStatus.PUBLISHED,
                "분기 보안 교육 확인 요청",
                "6월 21일까지 보안 교육을 수강하고 확인 처리를 완료해 주세요.",
                "## 확인 필요\n\n분기 보안 교육은 전 임직원 필수 과정입니다.",
                CATEGORY_SECURITY,
                "Security Ops",
                true,
                true
        );
        notice.setPublishedAt(Instant.now().minusSeconds(21_600));
        notice.getTags().addAll(List.of("notice", "security"));
        notice.getAudiences().add("ALL_EMPLOYEES");
        repository.save(notice);
        recordVersion(notice, "Initial published notice", notice.getAuthorId());

        ContentItem draft = newItem(
                ContentType.ARTICLE,
                ContentStatus.IN_REVIEW,
                "API 스타일 가이드 초안",
                "사내 REST API 네이밍과 오류 응답 규칙을 표준화합니다.",
                "## 검토 범위\n\n엔드포인트 네이밍, 페이지네이션, 오류 코드를 포함합니다.",
                CATEGORY_ENGINEERING,
                "콘텐츠 편집자",
                false,
                false
        );
        draft.getTags().addAll(List.of("api", "standard"));
        draft.getAudiences().add("DEPARTMENT:Engineering");
        repository.save(draft);
        recordVersion(draft, "Review requested", draft.getAuthorId());

        auditService.recordSystem(ContentAuditEvents.CONTENT_PUBLISHED, "ContentItem", notice.getId().toString(), "Local seed published required notice");
    }

    public List<ContentItem> list(ContentType type, ContentStatus status, String query) {
        String normalized = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        return repository.findAll().stream()
                .filter(item -> type == null || item.getContentType() == type)
                .filter(item -> status == null || item.getStatus() == status)
                .filter(item -> normalized.isBlank() || searchable(item).contains(normalized))
                .sorted(Comparator.comparing(ContentItem::getUpdatedAt).reversed())
                .toList();
    }

    public ContentItem get(UUID contentId) {
        return repository.findById(contentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "콘텐츠를 찾을 수 없습니다."));
    }

    public ContentItem createDraft(ContentUpsertRequest request, AuthUser actor) {
        validateRequest(request);
        ContentItem item = new ContentItem();
        applyRequest(item, request, actor);
        item.setStatus(ContentStatus.DRAFT);
        item.setCreatedBy(actor.id());
        repository.save(item);
        recordVersion(item, "Draft created", actor.id());
        auditService.record(actor, ContentAuditEvents.CONTENT_CREATED, "ContentItem", item.getId().toString(), item.getTitle());
        return item;
    }

    public ContentItem updateDraft(UUID contentId, ContentUpsertRequest request, AuthUser actor) {
        validateRequest(request);
        ContentItem item = get(contentId);
        if (request.baseVersionNumber() != null && request.baseVersionNumber() != item.getVersionNumber()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "최신 버전과 충돌했습니다.");
        }
        applyRequest(item, request, actor);
        item.incrementVersion();
        item.touch(actor.id());
        repository.save(item);
        recordVersion(item, request.changeSummary() == null ? "Draft updated" : request.changeSummary(), actor.id());
        auditService.record(actor, ContentAuditEvents.CONTENT_UPDATED, "ContentItem", item.getId().toString(), item.getTitle());
        return item;
    }

    public void deleteDraft(UUID contentId, AuthUser actor) {
        ContentItem item = get(contentId);
        if (item.getStatus() != ContentStatus.DRAFT && item.getStatus() != ContentStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "발행 전 초안만 삭제할 수 있습니다.");
        }
        repository.delete(contentId);
        auditService.record(actor, "CONTENT_DELETED", "ContentItem", contentId.toString(), item.getTitle());
    }

    public List<ContentVersion> versions(UUID contentId) {
        get(contentId);
        return versions.getOrDefault(contentId, List.of()).stream()
                .sorted(Comparator.comparing(ContentVersion::getVersionNumber).reversed())
                .toList();
    }

    public void recordVersion(ContentItem item, String changeSummary, UUID actorId) {
        versions.computeIfAbsent(item.getId(), ignored -> new ArrayList<>())
                .add(new ContentVersion(item.getId(), item.getVersionNumber(), item.getTitle(), item.getSummary(), item.getBody(), changeSummary, actorId));
    }

    private ContentItem newItem(ContentType type, ContentStatus status, String title, String summary, String body, UUID categoryId, String authorName, boolean important, boolean requiresAck) {
        ContentItem item = new ContentItem();
        item.setContentType(type);
        item.setStatus(status);
        item.setTitle(title);
        item.setSlug(slugify(title));
        item.setSummary(summary);
        item.setBody(body);
        item.setCategoryId(categoryId);
        item.setCategoryName(CATEGORY_NAMES.getOrDefault(categoryId, "General"));
        item.setAuthorId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        item.setAuthorName(authorName);
        item.setImportant(important);
        item.setRequiresAcknowledgement(requiresAck);
        return item;
    }

    private void applyRequest(ContentItem item, ContentUpsertRequest request, AuthUser actor) {
        item.setContentType(request.contentType());
        item.setTitle(request.title());
        item.setSlug(slugify(request.title()));
        item.setSummary(request.summary() == null ? "" : request.summary());
        item.setBody(request.body());
        item.setCategoryId(request.categoryId());
        item.setCategoryName(CATEGORY_NAMES.getOrDefault(request.categoryId(), "General"));
        item.setAuthorId(actor.id());
        item.setAuthorName(actor.displayName());
        item.setImportant(request.isImportant());
        item.setRequiresAcknowledgement(request.requiresAcknowledgement());
        item.setExpiresAt(request.expiresAt());
        item.getTags().clear();
        if (request.tags() != null) {
            item.getTags().addAll(request.tags());
        }
        item.getAudiences().clear();
        item.getAudiences().addAll(request.audiences());
    }

    private void validateRequest(ContentUpsertRequest request) {
        if (request.requiresAcknowledgement() && request.contentType() != ContentType.NOTICE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "확인 필요는 NOTICE 콘텐츠에만 설정할 수 있습니다.");
        }
        if (request.audiences() == null || request.audiences().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "공개 대상은 최소 하나 이상 필요합니다.");
        }
    }

    private String searchable(ContentItem item) {
        return String.join(" ", item.getTitle(), item.getSummary(), item.getBody(), item.getCategoryName(), String.join(" ", item.getTags()))
                .toLowerCase(Locale.ROOT);
    }

    private String slugify(String title) {
        String normalized = Normalizer.normalize(title, Normalizer.Form.NFKD)
                .replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]+", "-")
                .replaceAll("(^-|-$)", "")
                .toLowerCase(Locale.ROOT);
        return normalized.isBlank() ? UUID.randomUUID().toString() : normalized;
    }

    public record ContentUpsertRequest(
            @NotNull ContentType contentType,
            @NotBlank @Size(max = 150) String title,
            @Size(max = 300) String summary,
            @NotBlank String body,
            @NotNull UUID categoryId,
            List<String> tags,
            @NotNull List<String> audiences,
            boolean isImportant,
            boolean requiresAcknowledgement,
            Instant expiresAt,
            Integer baseVersionNumber,
            @Size(max = 500) String changeSummary
    ) {
    }

    public record SubmitReviewRequest(
            @NotNull UUID reviewerId,
            @NotBlank @Size(max = 500) String changeSummary
    ) {
    }

    public record ApprovalDecisionRequest(
            @NotNull com.company.cms.content.domain.ContentEnums.ApprovalDecision decision,
            @Size(max = 1000) String decisionComment
    ) {
    }

    public record PublishRequest(
            Instant scheduledAt,
            Instant expiresAt
    ) {
    }
}
