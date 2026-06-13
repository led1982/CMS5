package com.company.cms.common.api;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
    Instant timestamp,
    int status,
    String code,
    String message,
    List<FieldError> fieldErrors
) {
    public record FieldError(String field, String message) {
    }
}
