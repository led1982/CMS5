package com.company.cms.auth;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
    Optional<RoleEntity> findByCode(RoleCode code);

    List<RoleEntity> findByCodeIn(Collection<RoleCode> codes);
}
