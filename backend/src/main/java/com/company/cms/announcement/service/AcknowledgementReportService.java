package com.company.cms.announcement.service;

import com.company.cms.announcement.domain.AnnouncementAcknowledgement;
import com.company.cms.announcement.domain.AnnouncementAcknowledgementRepository;
import com.company.cms.content.api.ContentMapper;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.service.ContentWorkflowService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcknowledgementReportService {
    private final AnnouncementAcknowledgementRepository acknowledgementRepository;
    private final ContentWorkflowService contentWorkflowService;
    private final ContentMapper mapper;

    public AcknowledgementReportService(AnnouncementAcknowledgementRepository acknowledgementRepository,
            ContentWorkflowService contentWorkflowService, ContentMapper mapper) {
        this.acknowledgementRepository = acknowledgementRepository;
        this.contentWorkflowService = contentWorkflowService;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public AcknowledgementReport report(UUID contentId, UUID departmentId, Boolean acknowledged) {
        ContentItem content = contentWorkflowService.content(contentId);
        List<AcknowledgementRow> rows = acknowledgementRepository.findByContent(content).stream()
            .filter(row -> departmentId == null
                || (row.getUser().getDepartment() != null && row.getUser().getDepartment().getId().equals(departmentId)))
            .filter(row -> acknowledged == null || row.isAcknowledged() == acknowledged)
            .map(row -> new AcknowledgementRow(
                mapper.user(row.getUser()),
                row.getUser().getDepartment() == null ? null : mapper.department(row.getUser().getDepartment()),
                row.isAcknowledged(),
                row.getAcknowledgedAt()
            ))
            .toList();
        long acknowledgedCount = rows.stream().filter(AcknowledgementRow::acknowledged).count();
        return new AcknowledgementReport(content.getId(), content.getTitle(), rows.size(), acknowledgedCount, rows);
    }

    public record AcknowledgementReport(
        UUID contentId,
        String title,
        long totalTargets,
        long acknowledgedCount,
        List<AcknowledgementRow> rows
    ) {
    }

    public record AcknowledgementRow(
        com.company.cms.content.api.ContentDtos.UserSummary user,
        com.company.cms.content.api.ContentDtos.DepartmentDto department,
        boolean acknowledged,
        Instant acknowledgedAt
    ) {
    }
}
