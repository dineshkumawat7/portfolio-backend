package com.dinesh.portfolio.service.impl;

import com.dinesh.portfolio.dto.ContactUsRequestDTO;
import com.dinesh.portfolio.dto.ContactUsResponseDTO;
import com.dinesh.portfolio.entity.ContactUs;
import com.dinesh.portfolio.enums.ContactUsStatus;
import com.dinesh.portfolio.exception.DatabaseOperationException;
import com.dinesh.portfolio.exception.ResourceNotFoundException;
import com.dinesh.portfolio.exception.ServiceException;
import com.dinesh.portfolio.repository.ContactUsRepository;
import com.dinesh.portfolio.service.ContactUsService;
import com.dinesh.portfolio.service.EmailService;
import com.dinesh.portfolio.util.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ContactUsServiceImpl implements ContactUsService {

    private final ContactUsRepository contactUsRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    @Transactional
    @Override
    public ContactUsResponseDTO createContactUs(ContactUsRequestDTO contactUsRequestDTO) {
        log.info("Creating new contact-us record | email: {}", contactUsRequestDTO.getEmail());
        try {
            ContactUs contactUs = ContactUs.builder()
                    .ticketNumber(Utility.generateTicketNumber())
                    .name(contactUsRequestDTO.getName())
                    .email(contactUsRequestDTO.getEmail())
                    .subject(contactUsRequestDTO.getSubject())
                    .message(contactUsRequestDTO.getMessage())
                    .status(ContactUsStatus.NEW.name())
                    .emailSent(false)
                    .createdAt(Instant.now())
                    .build();
            ContactUs saved = contactUsRepository.save(contactUs);
            log.info("Contact-us record created successfully | id: {} | email: {}", saved.getId(), saved.getEmail());
            // sent acknowledgment email to user
            sendContactUsAcknowledgementEmail(saved.getId(), saved.getEmail());
            return modelMapper.map(saved, ContactUsResponseDTO.class);
        } catch (Exception ex) {
            log.error(
                    "Error while creating contact-us record | email: {} | error: {}",
                    contactUsRequestDTO.getEmail(),
                    ex.getMessage()
            );
            throw new ServiceException("Unable to create contact-us record", ex);
        }
    }

    @Transactional
    @Override
    public List<ContactUsResponseDTO> getAllContactUsDetails() {
        log.info("Fetching all contact-us records");
        try {
            List<ContactUs> contactUsList = contactUsRepository.findAll();
            List<ContactUsResponseDTO> responseList = contactUsList.stream()
                    .map(contactUs -> modelMapper.map(contactUs, ContactUsResponseDTO.class))
                    .toList();
            log.info(
                    "Contact-us records fetched successfully | totalRecords: {}",
                    responseList.size()
            );
            return responseList;
        } catch (Exception ex) {
            log.error("Error while fetching contact-us details | error: {}", ex.getMessage());
            throw new ServiceException("Unable to fetch contact-us details", ex);
        }
    }

    @Transactional
    @Override
    public ContactUsResponseDTO getContactUsDetailById(Long id) {
        log.info("Fetching contact-us record | id: {}", id);
        try {
            if (id == null) {
                log.error("Contact-us fetch request failed because id is null");
                throw new IllegalArgumentException("Contact-us id cannot be null");
            }

            log.debug("Calling repository layer to fetch contact-us record | id: {}", id);
            ContactUs contactUs = contactUsRepository.findById(id);
            if (contactUs == null) {
                log.warn("No contact-us record found | id: {}", id);
                throw new ResourceNotFoundException("Contact-us record not found");
            }
            log.debug("Mapping ContactUs entity to ContactUsResponseDTO | id: {}", id);
            ContactUsResponseDTO responseDTO = modelMapper.map(contactUs, ContactUsResponseDTO.class);
            log.info(
                    "Contact-us record fetched successfully | id: {} | email: {}",
                    responseDTO.getId(),
                    responseDTO.getEmail()
            );
            return responseDTO;
        } catch (ResourceNotFoundException ex) {
            log.warn("Contact-us record not found | id: {} | error: {}", id, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(
                    "Unexpected exception occurred while fetching contact-us record | id: {} | error: {}",
                    id,
                    ex.getMessage()
            );
            throw new ServiceException("Unable to fetch contact-us record", ex);
        }
    }

    @Transactional
    @Override
    public void deleteContactUsDetailsById(Long id) {
        log.info("Deleting contact-us record | id: {}", id);
        try {
            if (id == null) {
                log.error("Contact-us delete request failed because id is null");
                throw new IllegalArgumentException("Contact-us id cannot be null");
            }
            log.debug("Calling repository layer to delete contact-us record | id: {}", id);
            contactUsRepository.deleteById(id);
            log.info("Contact-us record deleted successfully | id: {}", id);
        } catch (Exception ex) {
            log.error(
                    "Unexpected exception occurred while deleting contact-us record | id: {} | error: {}",
                    id,
                    ex.getMessage()
            );
            throw new ServiceException("Unable to delete contact-us record", ex);
        }
    }

    @Async
    private void sendContactUsAcknowledgementEmail(Long id, String email) {
        log.info("Initiating contact-us acknowledgment email process | id: {} | email: {}", id, email);
        try {
            ContactUsResponseDTO contactUsDetail = getContactUsDetailById(id);
            Context context = new Context();
            context.setVariable("name", contactUsDetail.getName());
            context.setVariable("ticketId", contactUsDetail.getTicketNumber());
            context.setVariable("subject", contactUsDetail.getSubject());
            String htmlContent = templateEngine.process("contact-response-email", context);
            emailService.sendEmail(email, "Thank You For Contacting Me", htmlContent)
                    .thenAccept(success -> {
                        if (success) {
                            try {
                                ContactUs contactUs = contactUsRepository.findById(id);
                                contactUs.setEmailSent(true);
                                contactUs.setStatus(ContactUsStatus.IN_PROGRESS.name());
                                contactUs.setUpdatedAt(Instant.now());
                                contactUsRepository.update(contactUs);

                                log.info(
                                        "Contact-us acknowledgment email sent successfully and DB updated " +
                                                "| id: {} | email: {}", id, email
                                );
                            } catch (Exception ex) {
                                log.error(
                                        "Email sent but DB update failed for contact-us record " +
                                                "| id: {} | email: {} | error={}", id, email, ex.getMessage()
                                );
                                throw new DatabaseOperationException(
                                        "Email sent but DB update failed for contact-us record", ex
                                );
                            }
                        } else {
                            log.warn(
                                    "Failed to send contact-us acknowledgment email | id: {} | email: {}",
                                    id, email
                            );
                        }
                    })
                    .exceptionally(ex -> {
                        log.error(
                                "Unexpected error while sending contact-us email | id: {} | email: {} | error: {}",",
                                id, email, ex.getMessage(), ex
                        );
                        throw new ServiceException("Unexpected error while sending contact-us email", ex);
                    });
        } catch (Exception ex) {
            log.error(
                    "Failed to initiate contact-us email process | id: {} | email: {} | error: {}",
                    id, email, ex.getMessage(), ex
            );
            throw new ServiceException("Failed to process contact-us email", ex);
        }
    }
}
