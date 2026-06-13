package com.company.cms.content.repository;

import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.domain.ContentVersion;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentVersionRepository extends JpaRepository<ContentVersion, UUID> {
    @EntityGraph(attributePaths = {"createdBy", "createdBy.department", "createdBy.roles"})
    List<ContentVersion> findByContentOrderByVersionNumberDesc(ContentItem content);

    @EntityGraph(attributePaths = {"createdBy", "createdBy.department", "createdBy.roles"})
    Optional<ContentVersion> findTopByContentOrderByVersionNumberDesc(ContentItem content);

    @Override
    @EntityGraph(attributePaths = {"createdBy", "createdBy.department", "createdBy.roles"})
    Optional<ContentVersion> findById(UUID id);
}
