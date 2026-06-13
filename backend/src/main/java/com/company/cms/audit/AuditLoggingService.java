package com.company.cms.audit;

import com.company.cms.auth.AuthenticatedUser;
import com.company.cms.auth.UserAccount;
import com.company.cms.auth.UserAccountRepository;
import com.company.cms.common.api.ApiException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLoggingService {
    private final AuditLogRepository auditLogRepository;
    private final UserAccountRepository userAccountRepository;

    public AuditLoggingService(AuditLogRepository auditLogRepository, UserAccountRepository userAccountRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(AuthenticatedUser actor, AuditAction action, String targetType, UUID targetId, String summary) {
        UserAccount actorAccount = userAccountRepository.findById(actor.id())
            .orElseThrow(() -> ApiException.unauthorized("UNKNOWN_ACTOR", "Authenticated user is not registered."));
        auditLogRepository.save(new AuditLog(actorAccount, action, targetType, targetId, toJson(summary)));
    }

    private String toJson(String summary) {
        String escaped = summary == null ? "" : summary.replace("\\", "\\\\").replace("\"", "\\\"");
        return "{\"summary\":\"" + escaped + "\"}";
    }
}
