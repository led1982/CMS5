package com.acme.cms.security.repository;

import com.acme.cms.security.model.AudienceGroup;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AudienceGroupRepository extends JpaRepository<AudienceGroup, UUID> {
    List<AudienceGroup> findByActiveTrueOrderByNameAsc();
}
