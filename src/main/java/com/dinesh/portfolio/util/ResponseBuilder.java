package com.dinesh.portfolio.util;

import com.dinesh.portfolio.dto.response.CommonErrorResponse;
import com.dinesh.portfolio.dto.response.CommonSuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

public class ResponseBuilder {

    private ResponseBuilder() {
    }

    public static <T> CommonSuccessResponse<T> buildSuccessResponse(
            String message, T data, HttpStatus status, HttpServletRequest request
    ) {
        return CommonSuccessResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .status(status.value())
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .requestId(request.getRequestId())
                .build();
    }

    public static CommonErrorResponse buildErrorResponse(
            String message, List<String> errors, String errorCode, HttpStatus status, HttpServletRequest request
    ) {
        return CommonErrorResponse.builder()
                .success(false)
                .message(message)
                .errors(errors)
                .errorCode(errorCode)
                .status(status.value())
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .requestId(request.getRequestId())
                .build();
    }
}
