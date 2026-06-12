package com.acme.cms.portal.bookmark;

import com.acme.cms.api.ApiDtos;
import com.acme.cms.api.ApiException;
import com.acme.cms.api.CmsMapper;
import com.acme.cms.audit.AuditEventService;
import com.acme.cms.content.ContentLifecycleService;
import com.acme.cms.content.model.ContentItem;
import com.acme.cms.content.repository.ContentItemRepository;
import com.acme.cms.security.CurrentUser;
import com.acme.cms.security.model.UserAccount;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookmarkService {
    private final BookmarkRepository bookmarks;
    private final ContentItemRepository contentItems;
    private final ContentLifecycleService lifecycle;
    private final CurrentUser currentUser;
    private final CmsMapper mapper;
    private final AuditEventService audit;

    public BookmarkService(
        BookmarkRepository bookmarks,
        ContentItemRepository contentItems,
        ContentLifecycleService lifecycle,
        CurrentUser currentUser,
        CmsMapper mapper,
        AuditEventService audit
    ) {
        this.bookmarks = bookmarks;
        this.contentItems = contentItems;
        this.lifecycle = lifecycle;
        this.currentUser = currentUser;
        this.mapper = mapper;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public ApiDtos.ContentPage list() {
        UserAccount user = currentUser.get();
        var items = bookmarks.findByUserOrderByCreatedAtDesc(user).stream()
            .map(Bookmark::getContentItem)
            .filter(lifecycle::canViewCurrentUser)
            .map(item -> mapper.toContentSummary(item, lifecycle.noticeFor(item)))
            .toList();
        return new ApiDtos.ContentPage(items, 0, items.size(), items.size(), 1);
    }

    @Transactional
    public void add(UUID contentId) {
        UserAccount user = currentUser.get();
        ContentItem item = contentItems.findById(contentId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Content was not found."));
        if (!lifecycle.canView(user, item)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Content is not visible to this user.");
        }
        bookmarks.findByUserAndContentItem(user, item).orElseGet(() -> {
            Bookmark bookmark = new Bookmark();
            bookmark.setUser(user);
            bookmark.setContentItem(item);
            audit.record(user, "BOOKMARK_CREATED", "CONTENT", item.getId().toString(), item.getTitle());
            return bookmarks.save(bookmark);
        });
    }

    @Transactional
    public void remove(UUID contentId) {
        UserAccount user = currentUser.get();
        contentItems.findById(contentId).ifPresent(item -> {
            bookmarks.deleteByUserAndContentItem(user, item);
            audit.record(user, "BOOKMARK_REMOVED", "CONTENT", item.getId().toString(), item.getTitle());
        });
    }
}
