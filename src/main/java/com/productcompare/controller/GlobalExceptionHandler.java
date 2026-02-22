package com.productcompare.controller;

import com.productcompare.dto.ApiErrorResponse;
import com.productcompare.exception.DuplicateStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DuplicateStoreException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateStore(DuplicateStoreException exception) {
        log.warn("Duplicate store request rejected: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse("DUPLICATE_STORE", exception.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatus(ResponseStatusException exception) {
        log.warn("Request failed with status {}: {}", exception.getStatusCode(), exception.getReason(), exception);
        String reason = exception.getReason() == null ? "Request failed" : exception.getReason();
        String code;
        if (exception.getStatusCode().value() == 404) {
            code = "NOT_FOUND";
        } else if (exception.getStatusCode().is4xxClientError()) {
            code = "BAD_REQUEST";
        } else {
            code = "SERVER_ERROR";
        }
        return ResponseEntity.status(exception.getStatusCode())
                .body(new ApiErrorResponse(code, reason));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnhandled(Exception exception) {
        log.error("Unhandled server exception", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse("SERVER_ERROR", "Internal server error"));
    }
}
