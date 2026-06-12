package com.acme.cms.notice;

import com.acme.cms.api.ApiDtos;
import com.acme.cms.api.ApiException;
import com.acme.cms.api.CmsMapper;
import com.acme.cms.audit.AuditEventService;
import com.acme.cms.content.ContentLifecycleService;
import com.acme.cms.content.model.ContentItem;
import com.acme.cms.content.model.ContentType;
import com.acme.cms.content.model.ContentVersion;
import com.acme.cms.content.repository.ContentItemRepository;
import com.acme.cms.security.CurrentUser;
import com.acme.cms.security.model.UserAccount;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoticeService {
    private final ContentItemRepository contentItems;
    private final NoticeAcknowledgementRepository acknowledgements;
    private final ContentLifecycleService lifecycle;
    private final CurrentUser currentUser;
    private final CmsMapper mapper;
    private final AuditEventService audit;

    public NoticeService(
        ContentItemRepository contentItems,
        NoticeAcknowledgementRepository acknowledgements,
        ContentLifecycleService lifecycle,
        CurrentUser currentUser,
        CmsMapper mapper,
        AuditEventService audit
    ) {
        this.contentItems = contentItems;
        this.acknowledgements = acknowledgements;
        this.lifecycle = lifecycle;
        this.currentUser = currentUser;
        this.mapper = mapper;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public List<ApiDtos.NoticeRequiredItem> requiredForCurrentUser() {
        UserAccount user = currentUser.get();
        return contentItems.findAll().stream()
            .filter(item -> item.getType() == ContentType.NOTICE)
            .filter(item -> lifecycle.canView(user, item))
            .filter(item -> {
                var settings = lifecycle.noticeFor(item);
                return settings != null && settings.isRequiresAcknowledgement();
            })
            .map(item -> {
                ContentVersion version = lifecycle.latestVersion(item);
                var acknowledgement = acknowledgements.findByContentItemAndUserAndContentVersion(item, user, version);
                return new ApiDtos.NoticeRequiredItem(
                    mapper.toContentSummary(item, lifecycle.noticeFor(item)),
                    acknowledgement.isPresent(),
                    acknowledgement.map(NoticeAcknowledgement::getAcknowledgedAt).orElse(null)
                );
            })
            .toList();
    }

    @Transactional
    public ApiDtos.NoticeAcknowledgementDto acknowledge(UUID noticeId) {
        UserAccount user = currentUser.get();
        ContentItem item = contentItems.findById(noticeId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Notice was not found."));
        if (item.getType() != ContentType.NOTICE || !lifecycle.canView(user, item)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Notice was not found.");
        }
        var settings = lifecycle.noticeFor(item);
        if (settings == null || !settings.isRequiresAcknowledgement()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "NOTICE_NOT_REQUIRED", "This notice does not require acknowledgement.");
        }
        ContentVersion version = lifecycle.latestVersion(item);
        NoticeAcknowledgement acknowledgement = acknowledgements
            .findByContentItemAndUserAndContentVersion(item, user, version)
            .orElseGet(() -> {
                NoticeAcknowledgement created = new NoticeAcknowledgement();
                created.setContentItem(item);
                created.setUser(user);
                created.setContentVersion(version);
                audit.record(user, "NOTICE_ACKNOWLEDGED", "CONTENT", item.getId().toString(), item.getTitle());
                return acknowledgements.save(created);
            });
        return new ApiDtos.NoticeAcknowledgementDto(
            item.getId().toString(),
            user.getId().toString(),
            acknowledgement.getAcknowledgedAt(),
            version.getId().toString()
        );
    }
}
