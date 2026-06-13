package com.company.cms.attachment;

import com.company.cms.audit.AuditAction;
import com.company.cms.auth.AuthenticatedUser;
import com.company.cms.auth.CurrentUserProvider;
import com.company.cms.auth.UserAccount;
import com.company.cms.auth.UserAccountRepository;
import com.company.cms.common.api.ApiException;
import com.company.cms.content.domain.Attachment;
import com.company.cms.content.domain.AttachmentStatus;
import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.domain.ContentVersion;
import com.company.cms.content.repository.AttachmentRepository;
import com.company.cms.content.service.ContentAuditPublisher;
import com.company.cms.content.service.ContentWorkflowService;
import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AttachmentMetadataService {
    private static final long MAX_SINGLE_FILE_SIZE = 10L * 1024L * 1024L;

    private final StorageAdapter storageAdapter;
    private final AttachmentRepository attachmentRepository;
    private final ContentWorkflowService contentWorkflowService;
    private final CurrentUserProvider currentUserProvider;
    private final UserAccountRepository userAccountRepository;
    private final ContentAuditPublisher auditPublisher;

    public AttachmentMetadataService(StorageAdapter storageAdapter, AttachmentRepository attachmentRepository,
            ContentWorkflowService contentWorkflowService, CurrentUserProvider currentUserProvider,
            UserAccountRepository userAccountRepository, ContentAuditPublisher auditPublisher) {
        this.storageAdapter = storageAdapter;
        this.attachmentRepository = attachmentRepository;
        this.contentWorkflowService = contentWorkflowService;
        this.currentUserProvider = currentUserProvider;
        this.userAccountRepository = userAccountRepository;
        this.auditPublisher = auditPublisher;
    }

    @Transactional
    public Attachment upload(java.util.UUID contentId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw ApiException.badRequest("FILE_REQUIRED", "Attachment file is required.");
        }
        if (file.getSize() > MAX_SINGLE_FILE_SIZE) {
            throw new ApiException(org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE, "FILE_TOO_LARGE",
                "Single file limit is 10MB.");
        }
        AuthenticatedUser actor = currentUserProvider.currentUser();
        UserAccount uploader = userAccountRepository.findById(actor.id())
            .orElseThrow(() -> ApiException.unauthorized("USER_NOT_FOUND", "Authenticated user was not found."));
        ContentItem content = contentWorkflowService.content(contentId);
        ContentVersion version = contentWorkflowService.currentVersion(content);
        try {
            String storageKey = storageAdapter.store(file.getOriginalFilename(), file.getInputStream());
            Attachment attachment = attachmentRepository.save(new Attachment(
                content,
                version,
                file.getOriginalFilename(),
                file.getContentType() == null ? "application/octet-stream" : file.getContentType(),
                file.getSize(),
                storageKey,
                AttachmentStatus.READY,
                uploader
            ));
            auditPublisher.publish(actor, AuditAction.ATTACHMENT_UPLOAD, content, "Attachment uploaded.");
            return attachment;
        } catch (IOException exception) {
            throw ApiException.badRequest("ATTACHMENT_STORE_FAILED", "Attachment could not be stored.");
        }
    }
}
