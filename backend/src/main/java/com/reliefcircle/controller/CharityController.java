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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.reliefcircle.dto.CharityDto;
import com.reliefcircle.dto.PaginatedResponse;
import com.reliefcircle.dto.PaginationRequest;
import com.reliefcircle.exception.CharityException;
import com.reliefcircle.service.CharityService;
import com.reliefcircle.model.User;
import com.reliefcircle.repository.UserRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/charities")
public class CharityController {
    
    private final CharityService charityService;
    private final UserRepository userRepository;
    
    @Autowired
    public CharityController(CharityService charityService, UserRepository userRepository) {
        this.charityService = charityService;
        this.userRepository = userRepository;
    }
    
    /**
     * Get all charities with optional filtering by donorId or fundraiserId
     * @param donorId Optional donor ID to filter charities by donations
     * @param fundraiserId Optional fundraiser ID to filter charities by fundraiser
     * @param paginationRequest Pagination parameters
     * @param authentication The authentication object (required if donorId or fundraiserId is provided)
     * @return Paginated list of charities based on the provided filters
     */
    @GetMapping
    public ResponseEntity<PaginatedResponse<CharityDto>> getAllCharities(
            @RequestParam(required = false) UUID donorId,
            @RequestParam(required = false) UUID fundraiserId,
            @Valid PaginationRequest paginationRequest,
            Authentication authentication) {
        
        log.info("Get all charities request - donorId: {}, fundraiserId: {}, page: {}, size: {}", 
                donorId, fundraiserId, paginationRequest.getPageNumber(), paginationRequest.getPageSize());
        
        // Check authentication if donorId or fundraiserId is provided
        if (donorId != null || fundraiserId != null) {
            if (authentication == null) {
                log.warn("Authentication required for filtered charity list but no authentication provided");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            
            // Get the user from authentication
            User user;
            if (authentication.getPrincipal() instanceof User) {
                user = (User) authentication.getPrincipal();
                log.debug("Principal is a User instance: {}", user.getEmail());
            } else if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                log.debug("Principal is UserDetails instance, extracting user by email: {}", userDetails.getUsername());
                user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> {
                        log.error("User not found for email: {}", userDetails.getUsername());
                        return new IllegalStateException("User not found");
                    });
            } else {
                log.error("Unexpected principal type: {}", 
                    authentication.getPrincipal() != null ? authentication.getPrincipal().getClass().getName() : "null");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            
            // Check if the user has the right to access these resources
            if (donorId != null && !user.getId().equals(donorId) && !user.getRole().equals(User.UserRole.FUNDRAISER)) {
                log.warn("Access denied: User {} with role {} attempted to access donor data for {}", 
                    user.getId(), user.getRole(), donorId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            
            if (fundraiserId != null && !user.getId().equals(fundraiserId) && !user.getRole().equals(User.UserRole.FUNDRAISER)) {
                log.warn("Access denied: User {} with role {} attempted to access fundraiser data for {}", 
                    user.getId(), user.getRole(), fundraiserId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            
            log.debug("User {} authorized to access requested charity data", user.getId());
        }
        
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

        try {
            if (donorId != null) {
                log.debug("Fetching charities for donor: {}", donorId);
                page = charityService.getCharitiesByDonor(donorId, pageRequest);
            } else if (fundraiserId != null) {
                log.debug("Fetching charities for fundraiser: {}", fundraiserId);
                page = charityService.getCharitiesByFundraiser(fundraiserId, pageRequest);
            } else {
                log.debug("Fetching all charities");
                page = charityService.getAllCharities(pageRequest);
            }
        } catch (Exception e) {
            log.error("Error fetching charities: {}", e.getMessage(), e);
            throw e;
        }

        PaginatedResponse<CharityDto> response = PaginatedResponse.<CharityDto>builder()
            .content(page.getContent())
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();

        log.info("Returning {} charities (page {} of {})", 
            page.getNumberOfElements(), page.getNumber() + 1, page.getTotalPages());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get only verified charities (public endpoint)
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
     * Get a charity by ID (public endpoint)
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
    public ResponseEntity<?> registerCharity(
            @RequestParam("name") @NotBlank(message = "Charity name is required") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "organizationName", required = false) String organizationName,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "targetAmount", required = false) 
                @Positive(message = "Target amount must be positive") BigDecimal targetAmount,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Authentication authentication) {
        
        log.info("Register charity request - name: {}, organizationName: {}, category: {}", 
                name, organizationName, category);
        
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));

            if (user.getRole() != User.UserRole.FUNDRAISER) {
                return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Only users with FUNDRAISER role can create charities"));
            }

            CharityDto charityRequest = CharityDto.builder()
                    .name(name)
                    .description(description)
                    .organizationName(organizationName)
                    .category(category)
                    .targetAmount(targetAmount)
                    .file(file)
                    .fundraiserId(user.getId())
                    .build();
            
            CharityDto createdCharity = charityService.registerCharity(charityRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCharity);
        } catch (Exception ex) {
            throw new CharityException("Failed to register charity: " + ex.getMessage(), ex);
        }
    }

    /**
     * Update an existing charity
     * @param id Charity ID
     * @param dto Updated charity information
     * @return The updated charity
     */
    @PutMapping("/{id}")
    public ResponseEntity<CharityDto> updateCharity(
            @PathVariable("id") @Positive(message = "Charity ID must be positive") Long id,
            @Valid @RequestBody CharityDto dto,
            Authentication authentication) {
        log.info("Update charity request - id: {}, name: {}", id, dto.getName());
        
        try {
            Object principal = authentication.getPrincipal();
            if (!(principal instanceof User user)) {
                log.error("Invalid authentication type: {}", 
                    principal != null ? principal.getClass().getName() : "null");
                throw new IllegalArgumentException("Invalid authentication type");
            }

            if (user.getRole() != User.UserRole.FUNDRAISER) {
                log.warn("Access denied: User {} with role {} attempted to update charity {}", 
                    user.getId(), user.getRole(), id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            // Check if user is the fundraiser of this charity
            CharityDto existingCharity = charityService.getCharityById(id);
            if (!existingCharity.getFundraiserId().equals(user.getId())) {
                log.warn("Access denied: User {} attempted to update charity {} owned by fundraiser {}", 
                    user.getId(), id, existingCharity.getFundraiserId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            
            log.debug("User {} authorized to update charity {}", user.getId(), id);

            CharityDto updatedCharity = charityService.updateCharity(id, dto);
            return ResponseEntity.ok(updatedCharity);
        } catch (Exception ex) {
            throw new CharityException("Failed to update charity: " + ex.getMessage(), ex);
        }
    }
}

class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}