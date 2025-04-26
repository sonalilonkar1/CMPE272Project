package com.reliefcircle.controller;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.reliefcircle.dto.CharityDto;
import com.reliefcircle.dto.PaginatedResponse;
import com.reliefcircle.dto.PaginationRequest;
import com.reliefcircle.exception.CharityException;
import com.reliefcircle.service.CharityService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/api/charities")
public class CharityController {
    
    private final CharityService charityService;
    
    @Autowired
    public CharityController(CharityService charityService) {
        this.charityService = charityService;
    }
    
    /**
     * Get all charities with optional filtering by donorId or fundraiserId
     * @param donorId Optional donor ID to filter charities by donations
     * @param fundraiserId Optional fundraiser ID to filter charities by fundraiser
     * @param paginationRequest Pagination parameters
     * @return Paginated list of charities based on the provided filters
     */
    @GetMapping
    public ResponseEntity<PaginatedResponse<CharityDto>> getAllCharities(
            @RequestParam(required = false) UUID donorId,
            @RequestParam(required = false) UUID fundraiserId,
            @Valid PaginationRequest paginationRequest) {
        
        Page<CharityDto> page;
        PageRequest pageRequest = PageRequest.of(
            paginationRequest.getPageNumber(), 
            paginationRequest.getPageSize(),
            Sort.by(
                paginationRequest.getSortDirection().equals("asc") ? 
                    Sort.Direction.ASC : Sort.Direction.DESC,
                paginationRequest.getSortBy() != null ? 
                    paginationRequest.getSortBy() : "createdAt"
            )
        );

        if (donorId != null) {
            page = charityService.getCharitiesByDonor(donorId, pageRequest);
        } else if (fundraiserId != null) {
            page = charityService.getCharitiesByFundraiser(fundraiserId, pageRequest);
        } else {
            page = charityService.getAllCharities(pageRequest);
        }

        PaginatedResponse<CharityDto> response = PaginatedResponse.<CharityDto>builder()
            .content(page.getContent())
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();

        return ResponseEntity.ok(response);
    }
    
    /**
     * Get only verified charities
     * @param paginationRequest Pagination parameters
     * @return Paginated list of verified charities
     */
    @GetMapping("/verified")
    public ResponseEntity<PaginatedResponse<CharityDto>> getVerifiedCharities(
            @Valid PaginationRequest paginationRequest) {
        Page<CharityDto> page = charityService.getVerifiedCharities(
            PageRequest.of(paginationRequest.getPageNumber(), paginationRequest.getPageSize()));

        PaginatedResponse<CharityDto> response = PaginatedResponse.<CharityDto>builder()
            .content(page.getContent())
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get a charity by ID
     * @param id Charity ID
     * @return The charity with the specified ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CharityDto> getCharityById(
            @PathVariable("id") @Positive(message = "Charity ID must be positive") Long id) {
        return ResponseEntity.ok(charityService.getCharityById(id));
    }
    
    /**
     * Verify a charity by ID
     * @param id Charity ID
     * @return Success/failure response
     */
    @PutMapping("/{id}/verify")
    public ResponseEntity<String> verifyCharity(
            @PathVariable("id") @Positive(message = "Charity ID must be positive") Long id) {
        boolean success = charityService.verifyCharity(id);
        if (success) {
            return ResponseEntity.ok("Charity verified successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Charity not found or could not be verified");
        }
    }
    
    /**
     * Register a new charity
     * @param name Charity name
     * @param description Charity description
     * @param organizationName Charity organization name
     * @param category Charity category
     * @param targetAmount Charity target amount
     * @param file Logo or image file
     * @return The created charity
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, 
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CharityDto> registerCharity(
            @RequestParam("name") @NotBlank(message = "Charity name is required") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "organizationName", required = false) String organizationName,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "targetAmount", required = false) 
                @Positive(message = "Target amount must be positive") BigDecimal targetAmount,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        try {
            CharityDto charityRequest = CharityDto.builder()
                    .name(name)
                    .description(description)
                    .organizationName(organizationName)
                    .category(category)
                    .targetAmount(targetAmount)
                    .file(file)
                    .build();
            
            CharityDto createdCharity = charityService.registerCharity(charityRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCharity);
        } catch (Exception ex) {
            throw new CharityException("Failed to register charity: " + ex.getMessage(), ex);
        }
    }
}