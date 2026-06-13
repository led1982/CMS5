package com.company.cms.portal.bookmark;

import com.company.cms.auth.AuthenticatedUser;
import com.company.cms.auth.CurrentUserProvider;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.portal.service.PortalContentService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final CurrentUserProvider currentUserProvider;
    private final PortalContentService portalContentService;

    public BookmarkService(BookmarkRepository bookmarkRepository, CurrentUserProvider currentUserProvider,
            PortalContentService portalContentService) {
        this.bookmarkRepository = bookmarkRepository;
        this.currentUserProvider = currentUserProvider;
        this.portalContentService = portalContentService;
    }

    @Transactional
    public void bookmark(UUID contentId) {
        AuthenticatedUser user = currentUserProvider.currentUser();
        portalContentService.requireVisible(contentId);
        bookmarkRepository.save(new Bookmark(new BookmarkId(user.id(), contentId)));
    }

    @Transactional
    public void delete(UUID contentId) {
        AuthenticatedUser user = currentUserProvider.currentUser();
        bookmarkRepository.deleteById(new BookmarkId(user.id(), contentId));
    }

    @Transactional(readOnly = true)
    public List<ContentItem> bookmarks() {
        AuthenticatedUser user = currentUserProvider.currentUser();
        return bookmarkRepository.findById_UserId(user.id()).stream()
            .map(bookmark -> portalContentService.requireVisible(bookmark.getId().getContentId()))
            .toList();
    }
}
