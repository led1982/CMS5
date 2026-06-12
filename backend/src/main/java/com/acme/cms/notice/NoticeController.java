package com.acme.cms.notice;

import com.acme.cms.api.ApiDtos;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notices")
public class NoticeController {
    private final NoticeService notices;

    public NoticeController(NoticeService notices) {
        this.notices = notices;
    }

    @GetMapping("/required")
    List<ApiDtos.NoticeRequiredItem> required() {
        return notices.requiredForCurrentUser();
    }

    @PutMapping("/{noticeId}/acknowledgements")
    ApiDtos.NoticeAcknowledgementDto acknowledge(@PathVariable UUID noticeId) {
        return notices.acknowledge(noticeId);
    }
}
