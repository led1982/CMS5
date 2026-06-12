package com.acme.cms.audit;

import com.acme.cms.security.model.UserAccount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditEventService {
    private final AuditEventRepository auditEvents;

    public AuditEventService(AuditEventRepository auditEvents) {
        this.auditEvents = auditEvents;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(UserAccount actor, String action, String targetType, String targetId, String details) {
        AuditEvent event = new AuditEvent();
        event.setActor(actor);
        event.setAction(action);
        event.setTargetType(targetType);
        event.setTargetId(targetId);
        event.setOutcome(AuditOutcome.SUCCESS);
        event.setDetails(details);
        auditEvents.save(event);
    }
}
