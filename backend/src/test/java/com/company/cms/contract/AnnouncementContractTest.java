package com.company.cms.contract;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class AnnouncementContractTest {
    @Test
    void includesAnnouncementAcknowledgementEndpoints() throws IOException {
        String contract = Files.readString(Path.of("src/docs/openapi.yaml"));
        assertThat(contract)
            .contains("/portal/announcements")
            .contains("/portal/contents/{contentId}/acknowledgements")
            .contains("/cms/announcements/{contentId}/acknowledgements");
    }
}
