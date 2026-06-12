package com.acme.cms.reporting;

import com.acme.cms.api.ApiDtos;
import com.acme.cms.content.model.ContentStatus;
import com.acme.cms.content.repository.ContentItemRepository;
import com.acme.cms.content.repository.NoticeSettingsRepository;
import com.acme.cms.notice.NoticeAcknowledgementRepository;
import com.acme.cms.audit.AuditEventRepository;
import com.acme.cms.security.CurrentUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/analytics")
public class AnalyticsSummaryController {
    private final ContentItemRepository contentItems;
    private final NoticeSettingsRepository noticeSettings;
    private final NoticeAcknowledgementRepository acknowledgements;
    private final AuditEventRepository auditEvents;
    private final CurrentUser currentUser;

    public AnalyticsSummaryController(
        ContentItemRepository contentItems,
        NoticeSettingsRepository noticeSettings,
        NoticeAcknowledgementRepository acknowledgements,
        AuditEventRepository auditEvents,
        CurrentUser currentUser
    ) {
        this.contentItems = contentItems;
        this.noticeSettings = noticeSettings;
        this.acknowledgements = acknowledgements;
        this.auditEvents = auditEvents;
        this.currentUser = currentUser;
    }

    @GetMapping("/summary")
    ApiDtos.AnalyticsSummary summary() {
        currentUser.requirePermission("ADMIN_ACCESS");
        var allContent = contentItems.findAll();
        long published = allContent.stream().filter(item -> item.getStatus() == ContentStatus.PUBLISHED).count();
        long drafts = allContent.stream().filter(item -> item.getStatus() == ContentStatus.DRAFT).count();
        var requiredNotices = noticeSettings.findAll().stream()
            .filter(setting -> setting.isRequiresAcknowledgement())
            .toList();
        long acknowledgementCount = acknowledgements.count();
        double rate = requiredNotices.isEmpty() ? 1.0 : Math.min(1.0, acknowledgementCount / (double) requiredNotices.size());
        return new ApiDtos.AnalyticsSummary(published, drafts, requiredNotices.size(), rate, auditEvents.count());
    }
}
