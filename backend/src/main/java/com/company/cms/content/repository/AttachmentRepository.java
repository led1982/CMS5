package com.company.cms.content.repository;

import com.company.cms.content.domain.Attachment;
import com.company.cms.content.domain.AttachmentStatus;
import com.company.cms.content.domain.ContentItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    List<Attachment> findByContentAndStatus(ContentItem content, AttachmentStatus status);

    List<Attachment> findByContent(ContentItem content);
}
