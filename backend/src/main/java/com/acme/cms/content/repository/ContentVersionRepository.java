package com.acme.cms.content.repository;

import com.acme.cms.content.model.ContentItem;
import com.acme.cms.content.model.ContentVersion;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentVersionRepository extends JpaRepository<ContentVersion, UUID> {
    Optional<ContentVersion> findTopByContentItemOrderByVersionNumberDesc(ContentItem contentItem);
}
