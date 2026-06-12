package com.acme.cms.content.repository;

import com.acme.cms.content.model.Attachment;
import com.acme.cms.content.model.ContentItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    List<Attachment> findByContentItemAndActiveTrueOrderByUploadedAtDesc(ContentItem contentItem);
}
