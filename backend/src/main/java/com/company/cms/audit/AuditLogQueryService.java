package com.company.cms.audit;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogQueryService {
    private final AuditLogRepository auditLogRepository;

    public AuditLogQueryService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(readOnly = true)
    public List<AuditLog> search(AuditAction action, UUID targetId, UUID actorId) {
        if (action == null && targetId == null && actorId == null) {
            return auditLogRepository.findTop100ByOrderByCreatedAtDesc();
        }
        Specification<AuditLog> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (action != null) {
                predicates.add(builder.equal(root.get("action"), action));
            }
            if (targetId != null) {
                predicates.add(builder.equal(root.get("targetId"), targetId));
            }
            if (actorId != null) {
                predicates.add(builder.equal(root.get("actor").get("id"), actorId));
            }
            query.orderBy(builder.desc(root.get("createdAt")));
            return builder.and(predicates.toArray(Predicate[]::new));
        };
        return auditLogRepository.findAll(specification);
    }
}
