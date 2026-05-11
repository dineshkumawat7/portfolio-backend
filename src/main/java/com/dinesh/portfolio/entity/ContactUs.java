package com.dinesh.portfolio.entity;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactUs {
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
