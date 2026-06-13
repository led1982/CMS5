package com.company.cms.content.service;

import com.company.cms.common.api.ApiException;
import com.company.cms.content.domain.ContentAudience;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.domain.ContentType;
import com.company.cms.content.domain.VisibilityType;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ContentValidationService {
    public void validateForReview(ContentItem content, String body, List<ContentAudience> audiences) {
        if (isBlank(content.getTitle()) || content.getTitle().length() > 150) {
            throw ApiException.badRequest("TITLE_REQUIRED", "Title is required and must be 150 characters or fewer.");
        }
        if (isBlank(body)) {
            throw ApiException.badRequest("BODY_REQUIRED", "Body is required.");
        }
        if (content.getCategory() == null) {
            throw ApiException.badRequest("CATEGORY_REQUIRED", "Category is required.");
        }
        if (audiences == null || audiences.isEmpty()) {
            throw ApiException.badRequest("AUDIENCE_REQUIRED", "At least one visibility target is required.");
        }
        if (content.isRequiresAcknowledgement() && content.getType() != ContentType.ANNOUNCEMENT) {
            throw ApiException.badRequest("ACK_ONLY_FOR_ANNOUNCEMENT", "Acknowledgement is only available for announcements.");
        }
        for (ContentAudience audience : audiences) {
            validateAudience(audience);
        }
        validatePublishWindow(content.getPublishStartAt(), content.getPublishEndAt());
    }

    public void validatePublishWindow(Instant startAt, Instant endAt) {
        if (startAt != null && endAt != null && !endAt.isAfter(startAt)) {
            throw ApiException.badRequest("INVALID_PUBLISH_WINDOW", "Publish end time must be after start time.");
        }
    }

    private void validateAudience(ContentAudience audience) {
        if (audience.getVisibilityType() == VisibilityType.ALL_EMPLOYEES && audience.getTargetId() != null) {
            throw ApiException.badRequest("INVALID_AUDIENCE", "ALL_EMPLOYEES audience cannot include a target id.");
        }
        if (audience.getVisibilityType() != VisibilityType.ALL_EMPLOYEES && audience.getTargetId() == null) {
            throw ApiException.badRequest("INVALID_AUDIENCE", "Target id is required for scoped audiences.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
