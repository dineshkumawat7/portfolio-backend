package com.dinesh.portfolio.exception;

import com.dinesh.portfolio.dto.response.CommonErrorResponse;
import com.dinesh.portfolio.util.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception occurred at URI: {}", request.getRequestURI(), ex);
        CommonErrorResponse errorResponse = ResponseBuilder.buildErrorResponse(
                "Something went wrong. Please try again later.",
                List.of(ex.getMessage()),
                "INTERNAL_SERVER_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(RequestProcessingException.class)
    public ResponseEntity<CommonErrorResponse> handleRequestProcessingException(
            RequestProcessingException ex, HttpServletRequest request) {
        log.error("Unhandled exception occurred at URI: {}", request.getRequestURI(), ex);
        CommonErrorResponse errorResponse = ResponseBuilder.buildErrorResponse(
                "Request processing failed",
                List.of(ex.getMessage()),
                "REQUEST_PROCESSING_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<CommonErrorResponse> handleDatabaseException(
            DatabaseOperationException ex, HttpServletRequest request) {

        log.error("Database exception occurred", ex);
        CommonErrorResponse errorResponse = ResponseBuilder.buildErrorResponse(
                "Database operation failed",
                List.of(ex.getMessage()),
                "DATABASE_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<CommonErrorResponse> handleServiceException(ServiceException ex, HttpServletRequest request) {
        log.error("Unhandled exception occurred at URI: {}", request.getRequestURI(), ex);
        CommonErrorResponse errorResponse = ResponseBuilder.buildErrorResponse(
                "Database operation failed",
                List.of(ex.getMessage()),
                "DATABASE_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CommonErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request
    ) {
        log.warn("Resource not found. RequestId: {}, Error: {}", request.getRequestId(), ex.getMessage(), ex);
        CommonErrorResponse errorResponse = ResponseBuilder.buildErrorResponse(
                "Resource not found",
                List.of(ex.getMessage()),
                "RESOURCE_NOT_FOUND",
                HttpStatus.NOT_FOUND,
                request
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
