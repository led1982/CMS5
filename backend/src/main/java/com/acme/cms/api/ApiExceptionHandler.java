package com.acme.cms.api;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ApiException.class)
    ResponseEntity<ApiDtos.ErrorResponse> handleApi(ApiException ex) {
        return ResponseEntity.status(ex.status()).body(new ApiDtos.ErrorResponse(ex.code(), ex.getMessage(), List.of()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiDtos.ErrorResponse> handleDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ApiDtos.ErrorResponse("FORBIDDEN", "Caller lacks permission for this operation.", List.of()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiDtos.ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        var fields = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new ApiDtos.FieldError(error.getField(), error.getDefaultMessage()))
            .toList();
        return ResponseEntity.badRequest().body(new ApiDtos.ErrorResponse("VALIDATION_FAILED", "Request validation failed.", fields));
    }

    @ExceptionHandler({IllegalArgumentException.class, ConstraintViolationException.class})
    ResponseEntity<ApiDtos.ErrorResponse> handleBadRequest(Exception ex) {
        return ResponseEntity.badRequest().body(new ApiDtos.ErrorResponse("BAD_REQUEST", ex.getMessage(), List.of()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiDtos.ErrorResponse> handleConflict(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ApiDtos.ErrorResponse("DATA_CONFLICT", "The request conflicts with existing CMS data.", List.of()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<ApiDtos.ErrorResponse> handleMaxUpload(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
            .body(new ApiDtos.ErrorResponse("FILE_TOO_LARGE", "Single files are limited to 10MB and total requests to 20MB.", List.of()));
    }
}
