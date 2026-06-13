package com.company.cms.contract;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ContentWorkflowContractTest {
    @Test
    void includesWorkflowEndpoints() throws IOException {
        String contract = Files.readString(Path.of("src/docs/openapi.yaml"));
        assertThat(contract)
            .contains("/cms/contents/{contentId}/submit")
            .contains("/cms/contents/{contentId}/review")
            .contains("/cms/contents/{contentId}/publish")
            .contains("/cms/contents/{contentId}/archive");
    }
}
