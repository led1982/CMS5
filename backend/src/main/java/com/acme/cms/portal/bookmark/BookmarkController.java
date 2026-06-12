package com.acme.cms.portal.bookmark;

import com.acme.cms.api.ApiDtos;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarks;

    public BookmarkController(BookmarkService bookmarks) {
        this.bookmarks = bookmarks;
    }

    @GetMapping
    ApiDtos.ContentPage list() {
        return bookmarks.list();
    }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody ApiDtos.BookmarkCreateRequest request) {
        bookmarks.add(UUID.fromString(request.contentId()));
        return ResponseEntity.created(URI.create("/api/v1/bookmarks/" + request.contentId())).build();
    }

    @DeleteMapping("/{contentId}")
    ResponseEntity<Void> delete(@PathVariable UUID contentId) {
        bookmarks.remove(contentId);
        return ResponseEntity.noContent().build();
    }
}
