package com.company.cms.announcement.api;

import com.company.cms.announcement.service.AcknowledgementReportService;
import com.company.cms.announcement.service.AcknowledgementService;
import com.company.cms.auth.AuthenticatedUser;
import com.company.cms.auth.CurrentUserProvider;
import com.company.cms.content.api.ContentDtos.ContentSummary;
import com.company.cms.content.api.ContentMapper;
import com.company.cms.content.domain.ContentStatus;
import com.company.cms.content.domain.ContentType;
import com.company.cms.content.repository.ContentAudienceRepository;
import com.company.cms.content.repository.ContentItemRepository;
import com.company.cms.portal.service.VisibilityService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AnnouncementController {
    private final ContentItemRepository contentItemRepository;
    private final ContentAudienceRepository audienceRepository;
    private final CurrentUserProvider currentUserProvider;
    private final VisibilityService visibilityService;
    private final ContentMapper mapper;
    private final AcknowledgementService acknowledgementService;
    private final AcknowledgementReportService reportService;

    public AnnouncementController(ContentItemRepository contentItemRepository,
            ContentAudienceRepository audienceRepository,
            CurrentUserProvider currentUserProvider,
            VisibilityService visibilityService,
            ContentMapper mapper,
            AcknowledgementService acknowledgementService,
            AcknowledgementReportService reportService) {
        this.contentItemRepository = contentItemRepository;
        this.audienceRepository = audienceRepository;
        this.currentUserProvider = currentUserProvider;
        this.visibilityService = visibilityService;
        this.mapper = mapper;
        this.acknowledgementService = acknowledgementService;
        this.reportService = reportService;
    }

    @GetMapping("/portal/announcements")
    public List<ContentSummary> portalAnnouncements(
            @RequestParam(defaultValue = "false") boolean onlyUnacknowledged) {
        AuthenticatedUser user = currentUserProvider.currentUser();
        return contentItemRepository
            .findByStatusAndTypeOrderByPinnedDescPublishedAtDesc(ContentStatus.PUBLISHED, ContentType.ANNOUNCEMENT)
            .stream()
            .filter(content -> visibilityService.canView(content, audienceRepository.findByContent(content), user, Instant.now()))
            .filter(content -> !onlyUnacknowledged || !acknowledgementService.isAcknowledged(content.getId()))
            .map(content -> mapper.summary(content, acknowledgementService.isAcknowledged(content.getId())))
            .toList();
    }

    @PostMapping("/portal/contents/{contentId}/acknowledgements")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acknowledge(@PathVariable UUID contentId) {
        acknowledgementService.acknowledge(contentId);
    }

    @GetMapping("/cms/announcements/{contentId}/acknowledgements")
    @PreAuthorize("hasAnyRole('REVIEWER','ADMIN')")
    public AcknowledgementReportService.AcknowledgementReport report(
            @PathVariable UUID contentId,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) Boolean acknowledged) {
        return reportService.report(contentId, departmentId, acknowledged);
    }
}
