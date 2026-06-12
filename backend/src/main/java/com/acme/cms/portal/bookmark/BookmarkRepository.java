package com.acme.cms.portal.bookmark;

import com.acme.cms.content.model.ContentItem;
import com.acme.cms.security.model.UserAccount;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {
    List<Bookmark> findByUserOrderByCreatedAtDesc(UserAccount user);
    Optional<Bookmark> findByUserAndContentItem(UserAccount user, ContentItem contentItem);
    void deleteByUserAndContentItem(UserAccount user, ContentItem contentItem);
}
