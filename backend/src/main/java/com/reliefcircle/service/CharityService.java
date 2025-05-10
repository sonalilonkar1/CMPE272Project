package com.reliefcircle.service;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import com.reliefcircle.exception.ResourceNotFoundException;
import com.paypal.orders.Order;
import com.reliefcircle.config.PayPalConfig;
import com.reliefcircle.dto.CharityDto;
import com.reliefcircle.dto.DonationDto;
import com.reliefcircle.paypal.PaymentDetails;
import com.reliefcircle.repository.CharityRepository;
import com.reliefcircle.repository.DonationRepository;
import com.reliefcircle.repository.UserRepository;
import com.reliefcircle.repository.VerificationRepository;
import com.reliefcircle.dto.VerificationDto;
import com.reliefcircle.model.*;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.reliefcircle.exception.CharityException;

@Slf4j
@Service
public class CharityService {

    private final CharityRepository charityRepository;
    private final DonationRepository donationRepository;
    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;
    private final AWSService awsService;
    private final PayPalConfig payPalConfig;

    @Autowired
    public CharityService(CharityRepository charityRepository,
                          DonationRepository donationRepository,
                          UserRepository userRepository,
                          VerificationRepository verificationRepository,
                          AWSService awsService,
                          PayPalConfig payPalConfig) {
        this.charityRepository = charityRepository;
        this.donationRepository = donationRepository;
        this.userRepository = userRepository;
        this.verificationRepository = verificationRepository;
        this.awsService = awsService;
        this.payPalConfig = payPalConfig;
    }

    private CharityDto convert(Charity charity) {
        return CharityDto.builder()
            .id(charity.getId())
            .name(charity.getName())
            .description(charity.getDescription())
            .organizationName(charity.getOrganizationName())
            .category(charity.getCategory())
            .targetAmount(charity.getTargetAmount())
            .raisedAmount(charity.getRaisedAmount())
            .isVerified(charity.getIsVerified())
            .createdAt(charity.getCreatedAt())
            .fundraiserId(charity.getFundraiser() != null ? charity.getFundraiser().getId() : null)
            .fundraiserName(charity.getFundraiser() != null ? charity.getFundraiser().getFullName() : null)
            .fundraiserEmail(charity.getFundraiser() != null ? charity.getFundraiser().getEmail() : null)
            .fileUrl(charity.getFileUrl())
            .build();
    }

    private DonationDto convert(Donation donation) {
        return DonationDto.builder()
            .id(donation.getId())
            .donorId(donation.getDonor().getId())
            .donorName(donation.getDonor().getFullName())
            .donorEmail(donation.getDonor().getEmail())
            .charityId(donation.getCharity().getId())
            .charityName(donation.getCharity().getName())
            .amount(donation.getAmount())
            .paymentStatus(donation.getPaymentStatus())
            .transactionId(donation.getTransactionId())
            .status(donation.getStatus())
            .createdAt(donation.getCreatedAt())
            .build();
    }

    public CharityDto registerCharity(CharityDto dto) {
        log.info("Register Req: {}", dto);
        System.out.println("Target amount in DTO: " + dto.getTargetAmount());

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Charity name is required");
        }

        User fundraiser = userRepository.findById(dto.getFundraiserId())
        .orElseThrow(() -> new CharityException("Fundraiser not found"));
        if (fundraiser.getRole() != User.UserRole.FUNDRAISER) {
            throw new CharityException("Only users with FUNDRAISER role can create charities");
        }

        BigDecimal targetAmount = dto.getTargetAmount() != null ? dto.getTargetAmount() : BigDecimal.ZERO;
        System.out.println("Setting target amount to: " + targetAmount);

        Charity charity = Charity.builder()
            .name(dto.getName())
            .description(dto.getDescription() != null ? dto.getDescription() : "")
            .organizationName(dto.getOrganizationName() != null ? dto.getOrganizationName() : "")
            .category(dto.getCategory() != null ? dto.getCategory() : "General")
            .targetAmount(targetAmount)
            .raisedAmount(BigDecimal.ZERO)
            .isVerified(false)
            .fundraiser(fundraiser)
            .build();

        try {
            // Handle file upload if provided
            if (dto.getFile() != null && !dto.getFile().isEmpty()) {
                log.info("Uploading file: {}", dto.getFile().getOriginalFilename());
                String folderName = "charity-" + dto.getName().replaceAll("\\s+", "_");
                String s3Url = awsService.uploadProofDocument(dto.getFile(), folderName);
                
                // Save the file URL to the charity entity
                charity.setFileUrl(s3Url);
                log.info("File uploaded to S3: {}", s3Url);
            }

            // Save charity to the database
            Charity registeredCharity = charityRepository.save(charity);
            System.out.println("Saved charity with target amount: " + registeredCharity.getTargetAmount());
            log.info("Charity saved: {}", registeredCharity);
            
            // Convert to DTO for response
            CharityDto saved = convert(registeredCharity);
            
            // Set file URL in the response DTO
            if (registeredCharity.getFileUrl() != null) {
                saved.setFileUrl(registeredCharity.getFileUrl());
            }
            
            System.out.println("Converted DTO target amount: " + saved.getTargetAmount());
            
            // Notify fundraiser about the new charity
            awsService.pubMessageToFundraiser(saved);
            return saved;
        } catch (Exception e) {
            log.error("Error registering charity: {}", e.getMessage(), e);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to register charity: " + e.getMessage());
        }
    }

    public List<CharityDto> getAllCharities() {
        return charityRepository.findAll().stream()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    public Page<CharityDto> getAllCharities(PageRequest pageRequest) {
        return charityRepository.findAll(pageRequest)
            .map(this::convert);
    }

    public List<CharityDto> getVerifiedCharities() {
        return charityRepository.findAll().stream()
            .filter(charity -> charity.getIsVerified())
            .map(this::convert)
            .collect(Collectors.toList());
    }

    public Page<CharityDto> getVerifiedCharities(PageRequest pageRequest) {
        return charityRepository.findByIsVerified(true, pageRequest)
            .map(this::convert);
    }

    public boolean verifyCharity(long id) {
        Optional<Charity> charityOptional = charityRepository.findById(id);
        charityOptional.ifPresent(charity -> {
            charity.setIsVerified(true);
            charityRepository.save(charity);
        });
        return charityOptional.isPresent();
    }

    @Transactional
    public List<DonationDto> getDonations() {
        return donationRepository.findAll().stream()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    @Transactional
    public Page<DonationDto> getDonations(PageRequest pageRequest) {
        return donationRepository.findAll(pageRequest)
            .map(this::convert);
    }

    /**
     * Extract payment details from a PayPal Order object
     * 
     * @param order The PayPal Order object
     * @return PaymentDetails containing amount, email, and currency
     */
    private PaymentDetails extractPaymentDetails(Order order) {
        String email = order.payer().email();
        double amount = Double.parseDouble(order.purchaseUnits().get(0).amountWithBreakdown().value());
        String currencyCode = order.purchaseUnits().get(0).amountWithBreakdown().currencyCode();

        return new PaymentDetails(amount, email, currencyCode);
    }

    @Transactional
    public DonationDto addDonation(DonationDto donationDto) {
        User donor = userRepository.findById(donationDto.getDonorId())
                .orElseThrow(() -> new CharityException("Donor not found"));
        Charity charity = charityRepository.findById(donationDto.getCharityId())
                .orElseThrow(() -> new CharityException("Charity not found"));

        Donation donation = Donation.builder()
            .paypalOrderId(donationDto.getPaypalOrderId())
            .donor(donor)
            .charity(charity)
            .amount(donationDto.getAmount())
            .status(Donation.DonationStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .build();

        Donation savedDonation = donationRepository.save(donation);

        return DonationDto.builder()
                .id(savedDonation.getId())
                .paypalOrderId(savedDonation.getPaypalOrderId())
                .donorId(savedDonation.getDonor().getId())
                .donorName(savedDonation.getDonor().getFullName())
                .donorEmail(savedDonation.getDonor().getEmail())
                .charityId(savedDonation.getCharity().getId())
                .charityName(savedDonation.getCharity().getName())
                .amount(savedDonation.getAmount())
                .status(savedDonation.getStatus())
                .createdAt(savedDonation.getCreatedAt())
                .build();
    }

    @Transactional
    public VerificationDto submitVerification(Long verificationId, String comments, String status) {
        Verification verification = verificationRepository.findById(verificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Verification not found with ID: " + verificationId));

        Verification.VerificationStatus newStatus = Verification.VerificationStatus.valueOf(status.toUpperCase());
        verification.setStatus(newStatus);
        verification.setComment(comments);
        verification.setReviewedAt(LocalDateTime.now());

        Verification updated = verificationRepository.save(verification);
        return convertVerification(updated);
    }

    private VerificationDto convertVerification(Verification verification) {
        return VerificationDto.builder()
            .id(verification.getId())
            .volunteerId(verification.getVolunteer().getId())
            .volunteerName(verification.getVolunteer().getFullName())
            .volunteerEmail(verification.getVolunteer().getEmail())
            .charityId(verification.getCharity().getId())
            .charityName(verification.getCharity().getName())
            .proofId(verification.getProof() != null ? verification.getProof().getId() : null)
            .proofDescription(verification.getProof() != null ? verification.getProof().getDescription() : null)
            .status(verification.getStatus().name())
            .comment(verification.getComment())
            .reviewedAt(verification.getReviewedAt())
            .build();
    }

    @Transactional
    public List<DonationDto> getDonationsForDonor(UUID donorId) {
        return donationRepository.findByDonorId(donorId)
            .stream()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    @Transactional
    public Page<DonationDto> getDonationsForDonor(UUID donorId, PageRequest pageRequest) {
        return donationRepository.findByDonorId(donorId, pageRequest)
            .map(this::convert);
    }

    public List<CharityDto> getCharitiesByDonor(UUID donorId) {
        return donationRepository.findByDonorId(donorId)
            .stream()
            .map(Donation::getCharity)
            .distinct()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    public Page<CharityDto> getCharitiesByDonor(UUID donorId, PageRequest pageRequest) {
        return charityRepository.findDistinctByDonorId(donorId, pageRequest)
            .map(this::convert);
    }

    public List<CharityDto> getCharitiesByFundraiser(UUID fundraiserId) {
        return charityRepository.findByFundraiserId(fundraiserId)
            .stream()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    public Page<CharityDto> getCharitiesByFundraiser(UUID fundraiserId, PageRequest pageRequest) {
        return charityRepository.findByFundraiserId(fundraiserId, pageRequest)
            .map(this::convert);
    }

    public CharityDto getCharityById(Long id) {
        return charityRepository.findById(id)
            .map(this::convert)
            .orElseThrow(() -> new ResourceNotFoundException("Charity not found with ID: " + id));
    }

    @Transactional
    public CharityDto updateCharity(Long id, CharityDto dto) {
        Charity charity = charityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Charity not found with ID: " + id));

        // Update fields if provided
        if (dto.getName() != null) {
            charity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            charity.setDescription(dto.getDescription());
        }
        if (dto.getOrganizationName() != null) {
            charity.setOrganizationName(dto.getOrganizationName());
        }
        if (dto.getCategory() != null) {
            charity.setCategory(dto.getCategory());
        }
        if (dto.getTargetAmount() != null) {
            charity.setTargetAmount(dto.getTargetAmount());
        }

        // Handle file upload if provided
        if (dto.getFile() != null && !dto.getFile().isEmpty()) {
            try {
                log.info("Uploading file: {}", dto.getFile().getOriginalFilename());
                String folderName = "charity-" + dto.getName().replaceAll("\\s+", "_");
                String s3Url = awsService.uploadProofDocument(dto.getFile(), folderName);
                
                // Save the file URL to the charity entity
                charity.setFileUrl(s3Url);
                log.info("File uploaded to S3: {}", s3Url);
            } catch (Exception e) {
                log.error("Failed to upload charity image: {}", e.getMessage(), e);
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload charity image: " + e.getMessage());
            }
        }

        Charity updatedCharity = charityRepository.save(charity);
        return convert(updatedCharity);
    }

    public List<User> getDonorsWhoAreVolunteers() {
        return userRepository.findByRoleAndIsVolunteerTrue(User.UserRole.DONOR);
    }

    public Page<User> getDonorsWhoAreVolunteers(PageRequest pageRequest) {
        return userRepository.findByRoleAndIsVolunteerTrue(User.UserRole.DONOR, pageRequest);
    }

}