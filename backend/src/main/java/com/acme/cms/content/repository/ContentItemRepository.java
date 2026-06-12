package com.acme.cms.content.repository;

import com.acme.cms.content.model.ContentItem;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentItemRepository extends JpaRepository<ContentItem, UUID> {
}
