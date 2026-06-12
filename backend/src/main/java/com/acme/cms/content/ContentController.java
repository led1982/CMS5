package com.acme.cms.content;

import com.acme.cms.api.ApiDtos;
import com.acme.cms.api.CmsMapper;
import com.acme.cms.content.model.ContentStatus;
import com.acme.cms.content.model.ContentType;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/content")
public class ContentController {
    private final ContentLifecycleService lifecycle;
    private final AttachmentService attachments;
    private final CmsMapper mapper;

    public ContentController(ContentLifecycleService lifecycle, AttachmentService attachments, CmsMapper mapper) {
        this.lifecycle = lifecycle;
        this.attachments = attachments;
        this.mapper = mapper;
    }

    @GetMapping
    ApiDtos.ContentPage list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) ContentStatus status,
        @RequestParam(required = false) ContentType type,
        @RequestParam(required = false) String ownerUserId
    ) {
        var items = lifecycle.listForCurrentUser(status, type, ownerUserId);
        var paged = items.stream().skip((long) page * size).limit(size)
            .map(item -> mapper.toContentSummary(item, lifecycle.noticeFor(item)))
            .toList();
        return new ApiDtos.ContentPage(paged, page, size, items.size(), totalPages(items.size(), size));
    }

    @PostMapping
    ResponseEntity<ApiDtos.ContentDetail> create(@RequestBody ApiDtos.ContentCreateRequest request) {
        var item = lifecycle.create(request);
        return ResponseEntity.created(URI.create("/api/v1/content/" + item.getId())).body(toDetail(item.getId()));
    }

    @GetMapping("/{contentId}")
    ApiDtos.ContentDetail get(@PathVariable UUID contentId) {
        return toDetail(contentId);
    }

    @PatchMapping("/{contentId}")
    ApiDtos.ContentDetail update(@PathVariable UUID contentId, @RequestBody ApiDtos.ContentUpdateRequest request) {
        var item = lifecycle.update(contentId, request);
        return toDetail(item.getId());
    }

    @DeleteMapping("/{contentId}")
    ResponseEntity<Void> archiveViaDelete(@PathVariable UUID contentId) {
        lifecycle.archive(contentId, "Archived by delete endpoint");
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{contentId}/submit")
    ApiDtos.ContentDetail submit(@PathVariable UUID contentId) {
        var item = lifecycle.submit(contentId);
        return toDetail(item.getId());
    }

    @PostMapping("/{contentId}/approve")
    ApiDtos.ContentDetail approve(@PathVariable UUID contentId, @RequestBody(required = false) ApiDtos.LifecycleActionRequest request) {
        var item = lifecycle.approve(contentId, request == null ? null : request.note());
        return toDetail(item.getId());
    }

    @PostMapping("/{contentId}/publish")
    ApiDtos.ContentDetail publish(@PathVariable UUID contentId, @RequestBody(required = false) ApiDtos.PublishRequest request) {
        var item = lifecycle.publish(contentId, request);
        return toDetail(item.getId());
    }

    @PostMapping("/{contentId}/archive")
    ApiDtos.ContentDetail archive(@PathVariable UUID contentId, @RequestBody(required = false) ApiDtos.LifecycleActionRequest request) {
        var item = lifecycle.archive(contentId, request == null ? null : request.note());
        return toDetail(item.getId());
    }

    private ApiDtos.ContentDetail toDetail(UUID contentId) {
        var item = lifecycle.getVisibleOrManageable(contentId);
        return mapper.toContentDetail(item, lifecycle.latestVersion(item), attachments.activeFor(item), lifecycle.noticeFor(item));
    }

    private int totalPages(long totalItems, int size) {
        return (int) Math.ceil(totalItems / (double) size);
    }
}
