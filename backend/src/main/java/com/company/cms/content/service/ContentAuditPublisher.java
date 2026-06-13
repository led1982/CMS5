package com.company.cms.content.service;

import com.company.cms.audit.AuditAction;
import com.company.cms.audit.AuditLoggingService;
import com.company.cms.auth.AuthenticatedUser;
import com.company.cms.content.domain.ContentItem;
import org.springframework.stereotype.Component;

@Component
public class ContentAuditPublisher {
    private final AuditLoggingService auditLoggingService;

    public ContentAuditPublisher(AuditLoggingService auditLoggingService) {
        this.auditLoggingService = auditLoggingService;
    }

    public void publish(AuthenticatedUser actor, AuditAction action, ContentItem content, String summary) {
        auditLoggingService.record(actor, action, "ContentItem", content.getId(), summary);
    }
}
