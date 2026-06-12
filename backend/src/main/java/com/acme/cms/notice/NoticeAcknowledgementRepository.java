package com.acme.cms.notice;

import com.acme.cms.content.model.ContentItem;
import com.acme.cms.content.model.ContentVersion;
import com.acme.cms.security.model.UserAccount;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeAcknowledgementRepository extends JpaRepository<NoticeAcknowledgement, UUID> {
    Optional<NoticeAcknowledgement> findByContentItemAndUserAndContentVersion(
        ContentItem contentItem,
        UserAccount user,
        ContentVersion contentVersion
    );

    List<NoticeAcknowledgement> findByUser(UserAccount user);

    long countByContentItem(ContentItem contentItem);
}
