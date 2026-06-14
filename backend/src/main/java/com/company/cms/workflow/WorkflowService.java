package com.company.cms.workflow;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.company.cms.audit.AuditService;
import com.company.cms.auth.AuthUser;
import com.company.cms.content.domain.ContentEnums.ApprovalDecision;
import com.company.cms.content.domain.ContentEnums.ApprovalStatus;
import com.company.cms.content.domain.ContentEnums.AttachmentScanStatus;
import com.company.cms.content.domain.ContentEnums.ContentStatus;
import com.company.cms.content.domain.ContentEnums.PublicationStatus;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.service.ContentAuditEvents;
import com.company.cms.content.service.ContentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WorkflowService {
    private final ContentService contentService;
    private final AuditService auditService;
    private final ConcurrentMap<UUID, ApprovalTask> approvalTasks = new ConcurrentHashMap<>();

    public WorkflowService(ContentService contentService, AuditService auditService) {
        this.contentService = contentService;
        this.auditService = auditService;
    }

    public ApprovalTask submitForReview(UUID contentId, ContentService.SubmitReviewRequest request, AuthUser actor) {
        ContentItem item = contentService.get(contentId);
        if (item.getStatus() != ContentStatus.DRAFT && item.getStatus() != ContentStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "초안 또는 반려 상태만 검토 요청할 수 있습니다.");
        }
        item.setStatus(ContentStatus.IN_REVIEW);
        item.touch(actor.id());
        contentService.recordVersion(item, request.changeSummary(), actor.id());
        ApprovalTask task = new ApprovalTask(UUID.randomUUID(), contentId, request.reviewerId(), ApprovalStatus.PENDING, null, actor.id(), Instant.now(), null);
        approvalTasks.put(contentId, task);
        auditService.record(actor, ContentAuditEvents.CONTENT_SUBMITTED, "ContentItem", contentId.toString(), request.changeSummary());
        return task;
    }

    public ApprovalTask decide(UUID contentId, ContentService.ApprovalDecisionRequest request, AuthUser actor) {
        ContentItem item = contentService.get(contentId);
        ApprovalTask current = approvalTasks.get(contentId);
        if (current == null || current.status() != ApprovalStatus.PENDING || item.getStatus() != ContentStatus.IN_REVIEW) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "대기 중인 검토 작업이 없습니다.");
        }
        if (request.decision() == ApprovalDecision.REJECT && (request.decisionComment() == null || request.decisionComment().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "반려 의견은 필수입니다.");
        }
        ApprovalStatus status = request.decision() == ApprovalDecision.APPROVE ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED;
        item.setStatus(status == ApprovalStatus.APPROVED ? ContentStatus.APPROVED : ContentStatus.REJECTED);
        item.touch(actor.id());
        ApprovalTask updated = new ApprovalTask(current.id(), contentId, current.reviewerId(), status, request.decisionComment(), current.requestedBy(), current.requestedAt(), Instant.now());
        approvalTasks.put(contentId, updated);
        auditService.record(actor, status == ApprovalStatus.APPROVED ? ContentAuditEvents.CONTENT_APPROVED : ContentAuditEvents.CONTENT_REJECTED,
                "ContentItem", contentId.toString(), request.decisionComment() == null ? item.getTitle() : request.decisionComment());
        return updated;
    }

    public Publication publish(UUID contentId, ContentService.PublishRequest request, AuthUser actor) {
        ContentItem item = contentService.get(contentId);
        if (item.getStatus() != ContentStatus.APPROVED && item.getStatus() != ContentStatus.SCHEDULED && item.getStatus() != ContentStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "승인된 콘텐츠만 발행할 수 있습니다.");
        }
        boolean hasBlockedAttachment = item.getAttachments().stream().anyMatch(attachment -> attachment.getScanStatus() != AttachmentScanStatus.CLEAN);
        if (hasBlockedAttachment) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "검사 완료되지 않은 첨부가 있어 발행할 수 없습니다.");
        }
        Instant now = Instant.now();
        boolean scheduled = request.scheduledAt() != null && request.scheduledAt().isAfter(now);
        item.setStatus(scheduled ? ContentStatus.SCHEDULED : ContentStatus.PUBLISHED);
        item.setScheduledAt(request.scheduledAt());
        item.setExpiresAt(request.expiresAt());
        item.setPublishedAt(scheduled ? null : now);
        item.touch(actor.id());
        Publication publication = new Publication(UUID.randomUUID(), contentId, scheduled ? PublicationStatus.SCHEDULED : PublicationStatus.PUBLISHED, request.scheduledAt(), item.getPublishedAt(), request.expiresAt());
        auditService.record(actor, ContentAuditEvents.CONTENT_PUBLISHED, "ContentItem", contentId.toString(), item.getTitle());
        return publication;
    }

    public ContentItem archive(UUID contentId, AuthUser actor) {
        ContentItem item = contentService.get(contentId);
        item.setStatus(ContentStatus.ARCHIVED);
        item.setArchivedAt(Instant.now());
        item.touch(actor.id());
        auditService.record(actor, ContentAuditEvents.CONTENT_ARCHIVED, "ContentItem", contentId.toString(), item.getTitle());
        return item;
    }

    public record ApprovalTask(
            UUID id,
            UUID contentId,
            UUID reviewerId,
            ApprovalStatus status,
            String decisionComment,
            UUID requestedBy,
            Instant requestedAt,
            Instant decidedAt
    ) {
    }

    public record Publication(
            UUID id,
            UUID contentId,
            PublicationStatus publicationStatus,
            Instant scheduledAt,
            Instant publishedAt,
            Instant expiresAt
    ) {
    }
}
