package com.company.cms.portal.service;

import com.company.cms.auth.AuthenticatedUser;
import com.company.cms.auth.CurrentUserProvider;
import com.company.cms.auth.UserAccount;
import com.company.cms.auth.UserAccountRepository;
import com.company.cms.common.api.ApiException;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.repository.ContentAudienceRepository;
import com.company.cms.content.repository.ContentItemRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PortalContentService {
    private final ContentItemRepository contentItemRepository;
    private final ContentAudienceRepository audienceRepository;
    private final CurrentUserProvider currentUserProvider;
    private final VisibilityService visibilityService;
    private final ContentViewEventRepository viewEventRepository;
    private final UserAccountRepository userAccountRepository;

    public PortalContentService(ContentItemRepository contentItemRepository,
            ContentAudienceRepository audienceRepository,
            CurrentUserProvider currentUserProvider,
            VisibilityService visibilityService,
            ContentViewEventRepository viewEventRepository,
            UserAccountRepository userAccountRepository) {
        this.contentItemRepository = contentItemRepository;
        this.audienceRepository = audienceRepository;
        this.currentUserProvider = currentUserProvider;
        this.visibilityService = visibilityService;
        this.viewEventRepository = viewEventRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional
    public ContentItem getVisibleDetail(UUID contentId, String source) {
        ContentItem content = requireVisible(contentId);
        AuthenticatedUser actor = currentUserProvider.currentUser();
        UserAccount user = userAccountRepository.findById(actor.id())
            .orElseThrow(() -> ApiException.unauthorized("USER_NOT_FOUND", "Authenticated user was not found."));
        content.incrementViewCount();
        viewEventRepository.save(new ContentViewEvent(content, user, source == null ? "DIRECT_LINK" : source));
        return contentItemRepository.save(content);
    }

    @Transactional(readOnly = true)
    public ContentItem requireVisible(UUID contentId) {
        AuthenticatedUser actor = currentUserProvider.currentUser();
        ContentItem content = contentItemRepository.findWithCategoryById(contentId)
            .orElseThrow(() -> ApiException.notFound("CONTENT_NOT_FOUND", "Content was not found."));
        boolean visible = visibilityService.canView(content, audienceRepository.findByContent(content), actor, Instant.now());
        if (!visible) {
            throw ApiException.forbidden("CONTENT_NOT_VISIBLE", "This content is not available to the current user.");
        }
        return content;
    }
}
