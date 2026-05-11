package com.dinesh.portfolio.service.impl;

import com.dinesh.portfolio.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public CompletableFuture<Boolean> sendEmail(String to, String subject, String body) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                helper.setFrom(fromEmail);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(body, true);

                mailSender.send(mimeMessage);

                log.info("Email sent successfully to: {} with subject: {}", to, subject);
                return true;

            } catch (MessagingException | MailException ex) {
                log.error("Failed to send email to: {}", to, ex);
                return false;
            }
        });
    }

}
