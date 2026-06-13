package com.company.cms.contract;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class AdminContractTest {
    @Test
    void includesAdminGovernanceEndpoints() throws IOException {
        String contract = Files.readString(Path.of("src/docs/openapi.yaml"));
        assertThat(contract)
            .contains("/admin/users")
            .contains("/admin/audit-logs")
            .contains("/admin/metrics/content");
    }
}
