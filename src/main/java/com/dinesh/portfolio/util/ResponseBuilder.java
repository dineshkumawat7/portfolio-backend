package com.dinesh.portfolio.util;

import com.dinesh.portfolio.dto.response.CommonErrorResponse;
import com.dinesh.portfolio.dto.response.CommonSuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

import java.time.Instant;

public class ResponseBuilder {

    private ResponseBuilder() {
    }

    public static <T> CommonSuccessResponse<T> buildSuccessResponse(
            String message, T data, HttpStatus status, HttpServletRequest request
    ) {
        return CommonSuccessResponse.<T>builder()
                .success(Constant.SUCCESS_TAG)
                .message(message)
                .data(data)
                .status(status.value())
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .requestId(request.getRequestId())
                .build();
    }

    public static <T> CommonErrorResponse<T> buildErrorResponse(
            String message, T errors, String errorCode, HttpStatus status, HttpServletRequest request
    ) {
        return CommonErrorResponse.<T>builder()
                .success(Constant.FAILURE_TAG)
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
