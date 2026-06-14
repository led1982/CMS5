package com.company.cms.notices.api;

import java.util.List;
import java.util.UUID;

import com.company.cms.auth.AuthUser;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.notices.NoticeService;
import com.company.cms.notices.domain.NoticeAcknowledgement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NoticeController {
    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("/api/v1/portal/notices")
    List<ContentItem> portalNotices(@AuthenticationPrincipal AuthUser user) {
        return noticeService.listNotices(user);
    }

    @PutMapping("/api/v1/portal/notices/{noticeId}/acknowledgement")
    NoticeAcknowledgement acknowledge(@PathVariable UUID noticeId, @AuthenticationPrincipal AuthUser user) {
        return noticeService.acknowledge(user, noticeId);
    }

    @GetMapping("/api/v1/admin/notices/{noticeId}/acknowledgements")
    @PreAuthorize("hasRole('ADMIN')")
    NoticeService.NoticeAcknowledgementReport report(@PathVariable UUID noticeId) {
        return noticeService.report(noticeId);
    }
}
