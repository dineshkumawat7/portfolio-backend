package com.dinesh.portfolio.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonSuccessResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private int status;
    private String path;
    private Instant timestamp;
    private String traceId;
}
