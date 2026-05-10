package com.dinesh.portfolio.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonErrorResponse {
    private boolean success;
    private String message;
    private List<String> errors;
    private String errorCode;
    private int status;
    private String path;
    private Instant timestamp;
    private String requestId;
}
