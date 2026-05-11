package com.dinesh.portfolio.service.impl;

import com.dinesh.portfolio.dto.ContactUsRequestDTO;
import com.dinesh.portfolio.dto.ContactUsResponseDTO;
import com.dinesh.portfolio.entity.ContactUs;
import com.dinesh.portfolio.exception.ResourceNotFoundException;
import com.dinesh.portfolio.exception.ServiceException;
import com.dinesh.portfolio.repository.ContactUsRepository;
import com.dinesh.portfolio.service.ContactUsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ContactUsServiceImpl implements ContactUsService {

    private final ContactUsRepository contactUsRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public ContactUsResponseDTO createContactUs(ContactUsRequestDTO contactUsRequestDTO) {
        log.info("Creating new contact-us record | email: {}", contactUsRequestDTO.getEmail());
        try {
            ContactUs contactUs = ContactUs.builder()
                    .name(contactUsRequestDTO.getName())
                    .email(contactUsRequestDTO.getEmail())
                    .subject(contactUsRequestDTO.getSubject())
                    .message(contactUsRequestDTO.getMessage())
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            ContactUs saved = contactUsRepository.save(contactUs);
            log.info("Contact-us record created successfully | id: {} | email: {}", saved.getId(), saved.getEmail());
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

}
