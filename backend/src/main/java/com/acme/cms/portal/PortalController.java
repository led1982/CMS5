package com.acme.cms.portal;

import com.acme.cms.api.ApiDtos;
import com.acme.cms.api.CmsMapper;
import com.acme.cms.content.AttachmentService;
import com.acme.cms.content.ContentLifecycleService;
import com.acme.cms.content.model.ContentType;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/portal")
public class PortalController {
    private final ContentLifecycleService lifecycle;
    private final AttachmentService attachments;
    private final CmsMapper mapper;

    public PortalController(ContentLifecycleService lifecycle, AttachmentService attachments, CmsMapper mapper) {
        this.lifecycle = lifecycle;
        this.attachments = attachments;
        this.mapper = mapper;
    }

    @GetMapping("/feed")
    ApiDtos.ContentPage feed(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) ContentType contentType,
        @RequestParam(required = false) String categoryId
    ) {
        var items = lifecycle.portalFeed(contentType, categoryId);
        var paged = items.stream().skip((long) page * size).limit(size)
            .map(item -> mapper.toContentSummary(item, lifecycle.noticeFor(item)))
            .toList();
        return new ApiDtos.ContentPage(paged, page, size, items.size(), totalPages(items.size(), size));
    }

    @GetMapping("/content/{contentId}")
    ApiDtos.ContentDetail detail(@PathVariable UUID contentId) {
        var item = lifecycle.getPortalDetail(contentId);
        return mapper.toContentDetail(item, lifecycle.latestVersion(item), attachments.activeFor(item), lifecycle.noticeFor(item));
    }

    private int totalPages(long totalItems, int size) {
        return (int) Math.ceil(totalItems / (double) size);
    }
}
