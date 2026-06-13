package com.company.cms.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class PerformanceSmokeTest {
    @Test
    void inMemorySearchSmokeCompletesQuickly() {
        Instant started = Instant.now();
        long count = IntStream.range(0, 50_000)
            .mapToObj(i -> "content-" + i)
            .filter(value -> value.contains("499"))
            .count();

        assertThat(count).isGreaterThan(0);
        assertThat(Duration.between(started, Instant.now()).toMillis()).isLessThan(1_000);
    }
}
