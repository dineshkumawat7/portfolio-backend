package com.dinesh.portfolio.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationErrors {
    private String field;
    private Object rejectedValue;
    private String message;
    private String code;
}
