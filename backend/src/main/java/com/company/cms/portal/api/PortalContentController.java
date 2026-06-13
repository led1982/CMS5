package com.company.cms.portal.api;

import com.company.cms.announcement.service.AcknowledgementService;
import com.company.cms.content.api.ContentDtos.ContentDetail;
import com.company.cms.content.api.ContentDtos.PagedContentSummary;
import com.company.cms.content.api.ContentMapper;
import com.company.cms.content.domain.AttachmentStatus;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.domain.ContentType;
import com.company.cms.content.repository.AttachmentRepository;
import com.company.cms.content.repository.ContentAudienceRepository;
import com.company.cms.content.service.ContentWorkflowService;
import com.company.cms.portal.bookmark.BookmarkService;
import com.company.cms.portal.service.PortalContentService;
import com.company.cms.portal.service.PortalSearchService;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/portal")
public class PortalContentController {
    private final PortalSearchService portalSearchService;
    private final PortalContentService portalContentService;
    private final BookmarkService bookmarkService;
    private final AcknowledgementService acknowledgementService;
    private final ContentWorkflowService workflowService;
    private final ContentMapper mapper;
    private final ContentAudienceRepository audienceRepository;
    private final AttachmentRepository attachmentRepository;

    public PortalContentController(PortalSearchService portalSearchService, PortalContentService portalContentService,
            BookmarkService bookmarkService, AcknowledgementService acknowledgementService,
            ContentWorkflowService workflowService, ContentMapper mapper,
            ContentAudienceRepository audienceRepository, AttachmentRepository attachmentRepository) {
        this.portalSearchService = portalSearchService;
        this.portalContentService = portalContentService;
        this.bookmarkService = bookmarkService;
        this.acknowledgementService = acknowledgementService;
        this.workflowService = workflowService;
        this.mapper = mapper;
        this.audienceRepository = audienceRepository;
        this.attachmentRepository = attachmentRepository;
    }

    @GetMapping("/contents")
    public PagedContentSummary search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) ContentType type,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) UUID ownerDepartmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "RELEVANCE") String sort) {
        return portalSearchService.search(q, type, categoryId, tag, ownerDepartmentId, page, size, sort);
    }

    @GetMapping("/contents/{contentId}")
    public ContentDetail detail(@PathVariable UUID contentId, @RequestParam(required = false) String source) {
        ContentItem content = portalContentService.getVisibleDetail(contentId, source);
        return mapper.detail(
            content,
            workflowService.currentVersion(content),
            audienceRepository.findByContent(content),
            attachmentRepository.findByContentAndStatus(content, AttachmentStatus.READY),
            acknowledgementService.isAcknowledged(contentId)
        );
    }

    @PostMapping("/contents/{contentId}/bookmarks")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void bookmark(@PathVariable UUID contentId) {
        bookmarkService.bookmark(contentId);
    }

    @DeleteMapping("/contents/{contentId}/bookmarks")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookmark(@PathVariable UUID contentId) {
        bookmarkService.delete(contentId);
    }
}
