package com.company.cms.attachment;

import java.io.IOException;
import java.io.InputStream;

public interface StorageAdapter {
    String store(String originalFilename, InputStream inputStream) throws IOException;
}
