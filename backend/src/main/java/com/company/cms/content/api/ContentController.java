package com.company.cms.content.api;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import com.company.cms.auth.AuthUser;
import com.company.cms.content.domain.ContentEnums.ContentStatus;
import com.company.cms.content.domain.ContentEnums.ContentType;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.domain.ContentVersion;
import com.company.cms.content.service.ContentService;
import com.company.cms.workflow.WorkflowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/content")
@PreAuthorize("hasAnyRole('ADMIN','EDITOR','REVIEWER')")
public class ContentController {
    private final ContentService contentService;
    private final WorkflowService workflowService;

    public ContentController(ContentService contentService, WorkflowService workflowService) {
        this.contentService = contentService;
        this.workflowService = workflowService;
    }

    @GetMapping
    List<ContentItem> list(
            @RequestParam(required = false) ContentType contentType,
            @RequestParam(required = false) ContentStatus status,
            @RequestParam(required = false) String q
    ) {
        return contentService.list(contentType, status, q);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    ContentItem create(@Valid @RequestBody ContentService.ContentUpsertRequest request, @AuthenticationPrincipal AuthUser user) {
        return contentService.createDraft(request, user);
    }

    @GetMapping("/{contentId}")
    ContentItem get(@PathVariable UUID contentId) {
        return contentService.get(contentId);
    }

    @PutMapping("/{contentId}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    ContentItem update(@PathVariable UUID contentId, @Valid @RequestBody ContentService.ContentUpsertRequest request, @AuthenticationPrincipal AuthUser user) {
        return contentService.updateDraft(contentId, request, user);
    }

    @DeleteMapping("/{contentId}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    ResponseEntity<Void> delete(@PathVariable UUID contentId, @AuthenticationPrincipal AuthUser user) {
        contentService.deleteDraft(contentId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{contentId}/versions")
    List<ContentVersion> versions(@PathVariable UUID contentId) {
        return contentService.versions(contentId);
    }

    @PostMapping("/{contentId}/submit")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    WorkflowService.ApprovalTask submit(@PathVariable UUID contentId, @Valid @RequestBody ContentService.SubmitReviewRequest request, @AuthenticationPrincipal AuthUser user) {
        return workflowService.submitForReview(contentId, request, user);
    }

    @PostMapping("/{contentId}/approval-decisions")
    @PreAuthorize("hasAnyRole('ADMIN','REVIEWER')")
    WorkflowService.ApprovalTask decide(@PathVariable UUID contentId, @Valid @RequestBody ContentService.ApprovalDecisionRequest request, @AuthenticationPrincipal AuthUser user) {
        return workflowService.decide(contentId, request, user);
    }

    @PostMapping("/{contentId}/publish")
    @PreAuthorize("hasAnyRole('ADMIN','REVIEWER')")
    WorkflowService.Publication publish(@PathVariable UUID contentId, @RequestBody ContentService.PublishRequest request, @AuthenticationPrincipal AuthUser user) {
        return workflowService.publish(contentId, request, user);
    }

    @PostMapping("/{contentId}/archive")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    ContentItem archive(@PathVariable UUID contentId, @AuthenticationPrincipal AuthUser user) {
        return workflowService.archive(contentId, user);
    }
}
