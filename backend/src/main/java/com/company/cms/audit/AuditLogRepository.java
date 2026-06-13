package com.company.cms.audit;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID>, JpaSpecificationExecutor<AuditLog> {
    @EntityGraph(attributePaths = {"actor", "actor.department", "actor.roles"})
    List<AuditLog> findTop100ByOrderByCreatedAtDesc();

    @Override
    @EntityGraph(attributePaths = {"actor", "actor.department", "actor.roles"})
    List<AuditLog> findAll(Specification<AuditLog> specification);
}
