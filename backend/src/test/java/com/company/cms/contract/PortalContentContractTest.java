package com.company.cms.contract;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class PortalContentContractTest {
    @Test
    void includesPortalSearchDetailAndBookmarkEndpoints() throws IOException {
        String contract = Files.readString(Path.of("src/docs/openapi.yaml"));
        assertThat(contract)
            .contains("/portal/contents")
            .contains("/portal/contents/{contentId}")
            .contains("/portal/contents/{contentId}/bookmarks");
    }
}
