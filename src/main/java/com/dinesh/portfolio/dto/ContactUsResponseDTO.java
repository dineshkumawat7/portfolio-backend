package com.dinesh.portfolio.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactUsResponseDTO {
    private Long id;
    private String ticketNumber;
    private String name;
    private String email;
    private String subject;
    private String message;
    private String status;
    private Boolean emailSent;
    private String remark;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant repliedAt;
}
