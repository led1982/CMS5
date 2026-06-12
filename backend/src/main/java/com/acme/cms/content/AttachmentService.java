package com.acme.cms.content;

import com.acme.cms.api.ApiException;
import com.acme.cms.content.model.Attachment;
import com.acme.cms.content.model.ContentItem;
import com.acme.cms.content.repository.AttachmentRepository;
import com.acme.cms.security.CurrentUser;
import com.acme.cms.security.model.UserAccount;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttachmentService {
    public static final long MAX_FILE_BYTES = 10L * 1024L * 1024L;
    public static final long MAX_REQUEST_BYTES = 20L * 1024L * 1024L;

    private final AttachmentRepository attachments;
    private final ContentLifecycleService lifecycle;
    private final CurrentUser currentUser;
    private final Path uploadRoot;

    public AttachmentService(
        AttachmentRepository attachments,
        ContentLifecycleService lifecycle,
        CurrentUser currentUser,
        @Value("${app.attachments.upload-dir}") String uploadDir
    ) {
        this.attachments = attachments;
        this.lifecycle = lifecycle;
        this.currentUser = currentUser;
        this.uploadRoot = Path.of(uploadDir);
    }

    @Transactional(readOnly = true)
    public List<Attachment> activeFor(ContentItem item) {
        return attachments.findByContentItemAndActiveTrueOrderByUploadedAtDesc(item);
    }

    public void validateUploadSize(long fileSize, long requestSize) {
        if (fileSize > MAX_FILE_BYTES || requestSize > MAX_REQUEST_BYTES) {
            throw new ApiException(HttpStatus.PAYLOAD_TOO_LARGE, "FILE_TOO_LARGE", "Single files are limited to 10MB and total requests to 20MB.");
        }
    }

    @Transactional(readOnly = true)
    public Attachment getDownloadable(UUID attachmentId) {
        UserAccount user = currentUser.get();
        Attachment attachment = attachments.findById(attachmentId)
            .filter(Attachment::isActive)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Attachment was not found."));
        if (!lifecycle.canView(user, attachment.getContentItem()) && !currentUser.hasPermission(user, "CONTENT_WRITE")) {
            throw new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Attachment was not found.");
        }
        return attachment;
    }

    public Path uploadRoot() {
        currentUser.requirePermission("CONTENT_WRITE");
        return uploadRoot;
    }
}
