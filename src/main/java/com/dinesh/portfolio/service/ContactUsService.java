package com.dinesh.portfolio.service;

import com.dinesh.portfolio.dto.ContactUsRequestDTO;
import com.dinesh.portfolio.dto.ContactUsResponseDTO;

import java.util.List;

public interface ContactUsService {
    ContactUsResponseDTO createContactUs(ContactUsRequestDTO contactUsRequestDTO);
    List<ContactUsResponseDTO> getAllContactUsDetails();
    ContactUsResponseDTO getContactUsDetailById(Long id);
    void deleteContactUsDetailsById(Long id);
}
