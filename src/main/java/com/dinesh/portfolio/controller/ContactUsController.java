package com.dinesh.portfolio.controller;

import com.dinesh.portfolio.dto.ContactUsRequestDTO;
import com.dinesh.portfolio.dto.ContactUsResponseDTO;
import com.dinesh.portfolio.dto.response.CommonSuccessResponse;
import com.dinesh.portfolio.service.ContactUsService;
import com.dinesh.portfolio.util.ResponseBuilder;
import com.dinesh.portfolio.util.Utility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/contact-us")
@Tag(
        name = "Contact Us APIs",
        description = "APIs for managing contact-us form submissions including create, fetch, and delete operations"
)
public class ContactUsController {

    private final ContactUsService contactUsService;

    @Operation(
            summary = "Create contact-us request",
            description = "Stores a new contact-us form submission in the database. Validates input " +
                    "and returns created record."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contact request created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonSuccessResponse<ContactUsResponseDTO>> createContactUs(
            @Valid @RequestBody ContactUsRequestDTO contactUsRequestDTO, HttpServletRequest request
    ) {
        log.info("Received Contact Us request | \n{}", Utility.toJson(contactUsRequestDTO));
        ContactUsResponseDTO contactUs = contactUsService.createContactUs(contactUsRequestDTO);
        CommonSuccessResponse<ContactUsResponseDTO> contactUsResponseDTO = ResponseBuilder.buildSuccessResponse(
                "Request submitted successfully", contactUs, HttpStatus.CREATED, request
        );
        log.info("Response prepared successfully for save contact-us | \n{}", Utility.toJson(contactUsResponseDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(contactUsResponseDTO);
    }

    @Operation(
            summary = "Get all contact-us records",
            description = "Fetches all contact-us form submissions sorted by latest first"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Records fetched successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonSuccessResponse<List<ContactUsResponseDTO>>> getAllContactUsDetails(
            HttpServletRequest request
    ) {
        log.info("Received request to fetch all contact-us records");
        List<ContactUsResponseDTO> contactUsDetails = contactUsService.getAllContactUsDetails();
        CommonSuccessResponse<List<ContactUsResponseDTO>> contactUsResponseDTO = ResponseBuilder.buildSuccessResponse(
                "Records fetched successfully", contactUsDetails, HttpStatus.OK, request
        );
        log.info("Response prepared successfully for contact-us records | totalRecords: {}", contactUsDetails.size());
        return ResponseEntity.status(HttpStatus.OK).body(contactUsResponseDTO);
    }

    @Operation(
            summary = "Get contact-us record by ID",
            description = "Fetches a single contact-us record based on unique ID. Returns 404 if not found."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Contact-us record not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonSuccessResponse<ContactUsResponseDTO>> getContactUsDetails(
            @Parameter(description = "Unique ID of contact-us record", example = "1")
            @PathVariable Long id, HttpServletRequest request
    ) {
        log.info("Received request to fetch contact-us record | id: {}", id);
        ContactUsResponseDTO contactUs = contactUsService.getContactUsDetailById(id);
        CommonSuccessResponse<ContactUsResponseDTO> contactUsResponseDTO = ResponseBuilder.buildSuccessResponse(
                "Record fetched successfully", contactUs, HttpStatus.OK, request
        );
        log.info("Response prepared successfully for contact-us record | /n{}", Utility.toJson(contactUsResponseDTO));
        return ResponseEntity.status(HttpStatus.OK).body(contactUsResponseDTO);
    }

    @Operation(
            summary = "Delete contact-us record by ID",
            description = "Deletes a contact-us record permanently from database. Returns success message if deleted."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Record not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonSuccessResponse<ContactUsResponseDTO>> deleteContactUsDetails(
            @Parameter(description = "Unique ID of contact-us record", example = "1")
            @PathVariable Long id, HttpServletRequest request
    ) {
        log.info("Received request to delete contact-us record | id: {}", id);
        contactUsService.deleteContactUsDetailsById(id);
        CommonSuccessResponse<ContactUsResponseDTO> response = ResponseBuilder.buildSuccessResponse(
                "Record deleted successfully", null, HttpStatus.OK, request
        );
        log.info("Response prepared successfully for delete contact-us request | /n{}", Utility.toJson(response));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
