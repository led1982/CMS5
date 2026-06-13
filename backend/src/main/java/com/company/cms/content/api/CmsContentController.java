package com.company.cms.content.api;

import com.company.cms.attachment.AttachmentMetadataService;
import com.company.cms.content.api.ContentDtos.AttachmentDto;
import com.company.cms.content.api.ContentDtos.ContentCreateRequest;
import com.company.cms.content.api.ContentDtos.ContentDetail;
import com.company.cms.content.api.ContentDtos.ContentSummary;
import com.company.cms.content.api.ContentDtos.ContentUpdateRequest;
import com.company.cms.content.api.ContentDtos.ContentVersionDto;
import com.company.cms.content.api.ContentDtos.PagedContentSummary;
import com.company.cms.content.api.ContentDtos.PublishRequest;
import com.company.cms.content.api.ContentDtos.ReviewRequest;
import com.company.cms.content.domain.Attachment;
import com.company.cms.content.domain.AttachmentStatus;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.domain.ContentStatus;
import com.company.cms.content.domain.ContentType;
import com.company.cms.content.domain.ContentVersion;
import com.company.cms.content.repository.AttachmentRepository;
import com.company.cms.content.repository.ContentAudienceRepository;
import com.company.cms.content.service.ContentWorkflowService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/cms/contents")
public class CmsContentController {
    private final ContentWorkflowService workflowService;
    private final ContentMapper mapper;
    private final ContentAudienceRepository audienceRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentMetadataService attachmentMetadataService;

    public CmsContentController(ContentWorkflowService workflowService, ContentMapper mapper,
            ContentAudienceRepository audienceRepository, AttachmentRepository attachmentRepository,
            AttachmentMetadataService attachmentMetadataService) {
        this.workflowService = workflowService;
        this.mapper = mapper;
        this.audienceRepository = audienceRepository;
        this.attachmentRepository = attachmentRepository;
        this.attachmentMetadataService = attachmentMetadataService;
    }

    @GetMapping
    public PagedContentSummary list(
            @RequestParam(required = false) ContentStatus status,
            @RequestParam(required = false) ContentType type,
            @RequestParam(required = false) UUID ownerDepartmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ContentItem> result = workflowService.list(status, type, ownerDepartmentId, page, size);
        List<ContentSummary> items = result.getContent().stream()
            .map(content -> mapper.summary(content, false))
            .toList();
        return new PagedContentSummary(items, result.getNumber(), result.getSize(), result.getTotalElements(),
            result.getTotalPages());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    public ContentDetail create(@Valid @RequestBody ContentCreateRequest request) {
        return detail(workflowService.create(request), false);
    }

    @GetMapping("/{contentId}")
    public ContentDetail get(@PathVariable UUID contentId) {
        return detail(workflowService.content(contentId), false);
    }

    @PatchMapping("/{contentId}")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    public ContentDetail update(@PathVariable UUID contentId, @Valid @RequestBody ContentUpdateRequest request) {
        return detail(workflowService.update(contentId, request), false);
    }

    @PostMapping("/{contentId}/submit")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    public ContentDetail submit(@PathVariable UUID contentId, @RequestBody(required = false) Map<String, String> body) {
        return detail(workflowService.submit(contentId, body == null ? null : body.get("changeNote")), false);
    }

    @PostMapping("/{contentId}/review")
    @PreAuthorize("hasAnyRole('REVIEWER','ADMIN')")
    public ContentDetail review(@PathVariable UUID contentId, @Valid @RequestBody ReviewRequest request) {
        return detail(workflowService.review(contentId, request), false);
    }

    @PostMapping("/{contentId}/publish")
    @PreAuthorize("hasAnyRole('REVIEWER','ADMIN')")
    public ContentDetail publish(@PathVariable UUID contentId, @Valid @RequestBody PublishRequest request) {
        return detail(workflowService.publish(contentId, request), false);
    }

    @PostMapping("/{contentId}/archive")
    @PreAuthorize("hasAnyRole('REVIEWER','ADMIN')")
    public ContentDetail archive(@PathVariable UUID contentId, @RequestBody(required = false) Map<String, String> body) {
        return detail(workflowService.archive(contentId, body == null ? null : body.get("reason")), false);
    }

    @GetMapping("/{contentId}/versions")
    public List<ContentVersionDto> versions(@PathVariable UUID contentId) {
        return workflowService.versions(contentId).stream().map(mapper::version).toList();
    }

    @PostMapping("/{contentId}/attachments")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    public AttachmentDto uploadAttachment(@PathVariable UUID contentId, @RequestParam("file") MultipartFile file) {
        Attachment attachment = attachmentMetadataService.upload(contentId, file);
        return mapper.attachment(attachment);
    }

    private ContentDetail detail(ContentItem content, boolean acknowledged) {
        ContentVersion version = workflowService.currentVersion(content);
        return mapper.detail(
            content,
            version,
            audienceRepository.findByContent(content),
            attachmentRepository.findByContentAndStatus(content, AttachmentStatus.READY),
            acknowledged
        );
    }
}
