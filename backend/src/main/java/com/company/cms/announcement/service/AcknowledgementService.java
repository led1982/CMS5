package com.company.cms.announcement.service;

import com.company.cms.announcement.domain.AnnouncementAcknowledgement;
import com.company.cms.announcement.domain.AnnouncementAcknowledgementRepository;
import com.company.cms.audit.AuditAction;
import com.company.cms.audit.AuditLoggingService;
import com.company.cms.auth.AuthenticatedUser;
import com.company.cms.auth.CurrentUserProvider;
import com.company.cms.auth.UserAccount;
import com.company.cms.auth.UserAccountRepository;
import com.company.cms.common.api.ApiException;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.domain.ContentType;
import com.company.cms.portal.service.PortalContentService;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcknowledgementService {
    private final AnnouncementAcknowledgementRepository acknowledgementRepository;
    private final PortalContentService portalContentService;
    private final CurrentUserProvider currentUserProvider;
    private final UserAccountRepository userAccountRepository;
    private final AuditLoggingService auditLoggingService;

    public AcknowledgementService(AnnouncementAcknowledgementRepository acknowledgementRepository,
            PortalContentService portalContentService,
            CurrentUserProvider currentUserProvider,
            UserAccountRepository userAccountRepository,
            AuditLoggingService auditLoggingService) {
        this.acknowledgementRepository = acknowledgementRepository;
        this.portalContentService = portalContentService;
        this.currentUserProvider = currentUserProvider;
        this.userAccountRepository = userAccountRepository;
        this.auditLoggingService = auditLoggingService;
    }

    @Transactional
    public void acknowledge(UUID contentId) {
        AuthenticatedUser actor = currentUserProvider.currentUser();
        ContentItem content = portalContentService.requireVisible(contentId);
        if (content.getType() != ContentType.ANNOUNCEMENT || !content.isRequiresAcknowledgement()) {
            throw ApiException.badRequest("ACK_NOT_REQUIRED", "This content does not require acknowledgement.");
        }
        UserAccount user = userAccountRepository.findById(actor.id())
            .orElseThrow(() -> ApiException.unauthorized("USER_NOT_FOUND", "Authenticated user was not found."));
        AnnouncementAcknowledgement acknowledgement = acknowledgementRepository
            .findByContentAndUser_Id(content, actor.id())
            .orElseGet(() -> new AnnouncementAcknowledgement(content, user));
        acknowledgement.acknowledge();
        acknowledgementRepository.save(acknowledgement);
        auditLoggingService.record(actor, AuditAction.ACKNOWLEDGE, "ContentItem", content.getId(),
            "Announcement acknowledged.");
    }

    @Transactional(readOnly = true)
    public boolean isAcknowledged(UUID contentId) {
        AuthenticatedUser actor = currentUserProvider.currentUser();
        ContentItem content = portalContentService.requireVisible(contentId);
        return acknowledgementRepository.findByContentAndUser_Id(content, actor.id())
            .map(AnnouncementAcknowledgement::isAcknowledged)
            .orElse(false);
    }
}
