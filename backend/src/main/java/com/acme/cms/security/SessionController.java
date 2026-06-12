package com.acme.cms.security;

import com.acme.cms.api.ApiDtos;
import com.acme.cms.api.CmsMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class SessionController {
    private final CurrentUser currentUser;
    private final CmsMapper mapper;

    public SessionController(CurrentUser currentUser, CmsMapper mapper) {
        this.currentUser = currentUser;
        this.mapper = mapper;
    }

    @GetMapping("/me")
    ApiDtos.UserSummary me() {
        return mapper.toUserSummary(currentUser.get());
    }
}
