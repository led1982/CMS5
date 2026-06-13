package com.company.cms.attachment;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocalFileStorageAdapter implements StorageAdapter {
    private final Path uploadDir;

    public LocalFileStorageAdapter(@Value("${cms.storage.upload-dir}") String uploadDir) {
        this.uploadDir = Path.of(uploadDir);
    }

    @Override
    public String store(String originalFilename, InputStream inputStream) throws IOException {
        Files.createDirectories(uploadDir);
        String safeName = originalFilename == null ? "attachment.bin" : originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        String storageKey = UUID.randomUUID() + "-" + safeName;
        Files.copy(inputStream, uploadDir.resolve(storageKey));
        return storageKey;
    }
}
