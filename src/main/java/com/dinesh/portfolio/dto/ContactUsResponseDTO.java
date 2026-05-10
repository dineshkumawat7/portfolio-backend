package com.dinesh.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactUsResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String subject;
    private String message;
    private Instant createdAt;
    private Instant updatedAt;
}
