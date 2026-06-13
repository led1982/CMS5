package com.company.cms.announcement.service;

import com.company.cms.announcement.domain.AnnouncementAcknowledgement;
import com.company.cms.announcement.domain.AnnouncementAcknowledgementRepository;
import com.company.cms.auth.AuthenticatedUser;
import com.company.cms.auth.RoleEntity;
import com.company.cms.auth.UserAccount;
import com.company.cms.auth.UserAccountRepository;
import com.company.cms.auth.UserStatus;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.repository.ContentAudienceRepository;
import com.company.cms.portal.service.VisibilityService;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcknowledgementTargetService {
    private final AnnouncementAcknowledgementRepository acknowledgementRepository;
    private final UserAccountRepository userAccountRepository;
    private final ContentAudienceRepository audienceRepository;
    private final VisibilityService visibilityService;

    public AcknowledgementTargetService(AnnouncementAcknowledgementRepository acknowledgementRepository,
            UserAccountRepository userAccountRepository, ContentAudienceRepository audienceRepository,
            VisibilityService visibilityService) {
        this.acknowledgementRepository = acknowledgementRepository;
        this.userAccountRepository = userAccountRepository;
        this.audienceRepository = audienceRepository;
        this.visibilityService = visibilityService;
    }

    @Transactional
    public void generateTargets(ContentItem content) {
        userAccountRepository.findByStatus(UserStatus.ACTIVE).stream()
            .filter(user -> visibilityService.canView(content, audienceRepository.findByContent(content), principal(user), Instant.now()))
            .filter(user -> !acknowledgementRepository.existsByContentAndUser_Id(content, user.getId()))
            .map(user -> new AnnouncementAcknowledgement(content, user))
            .forEach(acknowledgementRepository::save);
    }

    private AuthenticatedUser principal(UserAccount user) {
        Set<com.company.cms.auth.RoleCode> roles = user.getRoles().stream()
            .map(RoleEntity::getCode)
            .collect(Collectors.toSet());
        return new AuthenticatedUser(
            user.getId(),
            user.getEmail(),
            user.getDisplayName(),
            user.getDepartment() == null ? null : user.getDepartment().getId(),
            roles
        );
    }
}
