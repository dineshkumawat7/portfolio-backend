package com.dinesh.portfolio.service;

import com.dinesh.portfolio.dto.ContactUsResponseDTO;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
    CompletableFuture<Boolean> sendEmail(String to, String subject, String body);
}
