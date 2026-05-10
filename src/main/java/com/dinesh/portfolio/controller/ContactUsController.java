package com.dinesh.portfolio.controller;

import com.dinesh.portfolio.dto.ContactUsRequestDTO;
import com.dinesh.portfolio.dto.ContactUsResponseDTO;
import com.dinesh.portfolio.dto.response.CommonSuccessResponse;
import com.dinesh.portfolio.service.ContactUsService;
import com.dinesh.portfolio.util.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/contact-us")
public class ContactUsController {

    private final ContactUsService contactUsService;

    @PostMapping
    public ResponseEntity<CommonSuccessResponse<ContactUsResponseDTO>> createContactUs(
            @Valid @RequestBody ContactUsRequestDTO contactUsRequestDTO, HttpServletRequest request
    ) {
        log.info("Received Contact Us request | email={}", contactUsRequestDTO.getEmail());
        ContactUsResponseDTO contactUs = contactUsService.createContactUs(contactUsRequestDTO);
        CommonSuccessResponse<ContactUsResponseDTO> contactUsResponseDTO = ResponseBuilder.buildSuccessResponse(
                "Request submitted successfully", contactUs, HttpStatus.CREATED, request
        );
        log.info("Response prepared successfully for save contact-us | email: {}", contactUsRequestDTO.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(contactUsResponseDTO);
    }

    @GetMapping
    public ResponseEntity<CommonSuccessResponse<List<ContactUsResponseDTO>>> getAllContactUsDetails(
            HttpServletRequest request
    ) {
        log.info("Received request to fetch all contact-us records");
        List<ContactUsResponseDTO> contactUsDetails = contactUsService.getAllContactUsDetails();
        CommonSuccessResponse<List<ContactUsResponseDTO>> contactUsResponseDTO = ResponseBuilder.buildSuccessResponse(
                "Records fetched successfully", contactUsDetails, HttpStatus.OK, request
        );
        log.info("Response prepared successfully for contact-us records");
        return ResponseEntity.status(HttpStatus.OK).body(contactUsResponseDTO);
    }

    @GetMapping("{id}")
    public ResponseEntity<CommonSuccessResponse<ContactUsResponseDTO>> getContactUsDetails(
            @PathVariable Long id, HttpServletRequest request
    ) {
        log.info("Received request to fetch contact-us record | id={}", id);
        ContactUsResponseDTO contactUs = contactUsService.getContactUsDetailById(id);
        CommonSuccessResponse<ContactUsResponseDTO> contactUsResponseDTO = ResponseBuilder.buildSuccessResponse(
                "Record fetched successfully", contactUs, HttpStatus.OK, request
        );
        log.info("Response prepared successfully for contact-us record | Id {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(contactUsResponseDTO);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<CommonSuccessResponse<ContactUsResponseDTO>> deleteContactUsDetails(
            @PathVariable Long id, HttpServletRequest request
    ) {
        log.info("Received request to delete contact-us record | id={}", id);
        contactUsService.deleteContactUsDetailsById(id);
        CommonSuccessResponse<ContactUsResponseDTO> response = ResponseBuilder.buildSuccessResponse(
                "Record deleted successfully", null, HttpStatus.OK, request
        );
        log.info("Response prepared successfully for delete contact-us request | id={}", id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
