package com.company.cms.content.repository;

import com.company.cms.content.domain.ContentAudience;
import com.company.cms.content.domain.ContentItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentAudienceRepository extends JpaRepository<ContentAudience, UUID> {
    List<ContentAudience> findByContent(ContentItem content);

    void deleteByContent(ContentItem content);
}
