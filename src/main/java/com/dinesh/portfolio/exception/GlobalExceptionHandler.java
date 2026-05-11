package com.dinesh.portfolio.exception;

import com.dinesh.portfolio.dto.response.CommonErrorResponse;
import com.dinesh.portfolio.dto.response.ValidationErrors;
import com.dinesh.portfolio.util.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonErrorResponse<String>> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred | error: {}", ex.getMessage(), ex);
        CommonErrorResponse<String> errorResponse = ResponseBuilder.buildErrorResponse(
                "Something went wrong. Please try again later.",
                ex.getMessage(),
                "INTERNAL_SERVER_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(RequestProcessingException.class)
    public ResponseEntity<CommonErrorResponse<String>> handleRequestProcessingException(
            RequestProcessingException ex, HttpServletRequest request) {
        log.error("Request processing failed | error : {}", ex.getMessage(), ex);
        CommonErrorResponse<String> errorResponse = ResponseBuilder.buildErrorResponse(
                "Request processing failed",
                ex.getMessage(),
                "REQUEST_PROCESSING_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<CommonErrorResponse<String>> handleDatabaseException(
            DatabaseOperationException ex, HttpServletRequest request) {

        log.error("Database operation failed | error: {}", ex.getMessage(), ex);
        CommonErrorResponse<String> errorResponse = ResponseBuilder.buildErrorResponse(
                "Database operation failed",
                ex.getMessage(),
                "DATABASE_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<CommonErrorResponse<String>> handleServiceException(ServiceException ex, HttpServletRequest request) {
        log.error("Service exception occurred | error: {}", ex.getMessage(), ex);
        CommonErrorResponse<String> errorResponse = ResponseBuilder.buildErrorResponse(
                "Database operation failed",
                ex.getMessage(),
                "DATABASE_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CommonErrorResponse<String>> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request
    ) {
        log.warn("Resource not found | error: {}", ex.getMessage(), ex);
        CommonErrorResponse<String> errorResponse = ResponseBuilder.buildErrorResponse(
                "Resource not found",
                ex.getMessage(),
                "RESOURCE_NOT_FOUND",
                HttpStatus.NOT_FOUND,
                request
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonErrorResponse<List<ValidationErrors>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request
    ) {
        log.error("Validation failed | error: {}", ex.getMessage(), ex);
        List<ValidationErrors> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> ValidationErrors.builder()
                        .field(fieldError.getField())
                        .rejectedValue(fieldError.getRejectedValue())
                        .message(fieldError.getDefaultMessage())
                        .code(fieldError.getCode())
                        .build())
                .collect(Collectors.toList());
        CommonErrorResponse<List<ValidationErrors>> errorResponse = ResponseBuilder.buildErrorResponse(
                "Validation failed",
                validationErrors,
                "VALIDATION_ERROR",
                HttpStatus.BAD_REQUEST,
                request
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
