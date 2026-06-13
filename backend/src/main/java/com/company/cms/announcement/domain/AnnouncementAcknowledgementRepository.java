package com.company.cms.announcement.domain;

import com.company.cms.content.domain.ContentItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementAcknowledgementRepository extends JpaRepository<AnnouncementAcknowledgement, UUID> {
    @EntityGraph(attributePaths = {"user", "user.department", "user.roles"})
    List<AnnouncementAcknowledgement> findByContent(ContentItem content);

    @EntityGraph(attributePaths = {"user", "user.department", "user.roles"})
    Optional<AnnouncementAcknowledgement> findByContentAndUser_Id(ContentItem content, UUID userId);

    boolean existsByContentAndUser_Id(ContentItem content, UUID userId);

    long countByContentAndAcknowledgedAtIsNotNull(ContentItem content);

    long countByContentAndAcknowledgedAtIsNull(ContentItem content);
}
