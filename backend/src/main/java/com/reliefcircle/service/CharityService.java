package com.reliefcircle.service;

import java.util.Date;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import com.reliefcircle.exception.ResourceNotFoundException;
import com.paypal.orders.Order;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.reliefcircle.config.PayPalConfig;
import com.reliefcircle.dto.CharityDto;
import com.reliefcircle.dto.DonationDto;
import com.reliefcircle.model.Charity;
import com.reliefcircle.model.Donation;
import com.reliefcircle.model.DonationStatus;
import com.reliefcircle.paypal.PaymentDetails;
import com.reliefcircle.repository.CharityRepository;
import com.reliefcircle.repository.DonationRepository;
import com.reliefcircle.repository.UserProfileRepository;
import com.reliefcircle.repository.VolunteerVerificationRepository;
import com.reliefcircle.dto.VolunteerVerificationDto;
import com.reliefcircle.model.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CharityService {

    private final CharityRepository charityRepository;
    private final DonationRepository donationRepository;
    private final UserProfileRepository userProfileRepository;
    private final VolunteerVerificationRepository volunteerVerificationRepository;
    private final AWSService awsService;
    private final PayPalConfig payPalConfig;

    @Autowired
    public CharityService(CharityRepository charityRepository,
                          DonationRepository donationRepository,
                          UserProfileRepository userProfileRepository,
                          VolunteerVerificationRepository volunteerVerificationRepository,
                          AWSService awsService,
                          PayPalConfig payPalConfig) {
        this.charityRepository = charityRepository;
        this.donationRepository = donationRepository;
        this.userProfileRepository = userProfileRepository;
        this.volunteerVerificationRepository = volunteerVerificationRepository;
        this.awsService = awsService;
        this.payPalConfig = payPalConfig;
    }

    private CharityDto convert(Charity charity) {
        return CharityDto.builder()
            .id(charity.getId())
            .approved(charity.isApproved())
            .cname(charity.getCharityName())
            .description(charity.getDescription())
            .email(charity.getEmail())
            .fileLink(charity.getFileLink())
            .location(charity.getLocation())
            .build();
    }

    private DonationDto convert(Donation donation) {
        return DonationDto.builder()
            .amount(donation.getAmount())
            .email(donation.getEmail())
            .id(donation.getId())
            .paypalId(donation.getPaypalId())
            .status(donation.getStatus() != null ? donation.getStatus().name() : null)
            .paymentDate(donation.getPaymentDate())
            .currencyCode(donation.getCurrencyCode())
            .build();
    }

    public CharityDto registerCharity(CharityDto dto) {
        log.info("Register Req: {}", dto);

        String key = (dto.getCname() + "_" + dto.getEmail() + "_" + dto.getFile().getOriginalFilename())
                .replaceAll("\\s+", "_");

        Charity charity = Charity.builder()
            .charityName(dto.getCname())
            .approved(false)
            .description(dto.getDescription())
            .email(dto.getEmail())
            .location(dto.getLocation())
            .fileLink(awsService.getCloudFrontUrl() + "/" + key)
            .build();

        boolean uploadSuccess = awsService.uploadFile(key, dto.getFile());

        if (uploadSuccess) {
            Charity registeredCharity = charityRepository.save(charity);
            log.info("Charity saved: {}", registeredCharity);
            CharityDto saved = convert(registeredCharity);
            awsService.pubMessageToAdmin(saved);
            return saved;
        } else {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot upload file");
        }
    }

    public List<CharityDto> getAllCharities() {
        return charityRepository.findAll().stream()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    public List<CharityDto> getApprovedCharities() {
        return charityRepository.findAll().stream()
            .filter(Charity::isApproved)
            .map(this::convert)
            .collect(Collectors.toList());
    }

    public boolean approveCharity(long id) {
        Optional<Charity> charityOptional = charityRepository.findById(id);
        charityOptional.ifPresent(charity -> {
            charity.setApproved(true);
            charityRepository.save(charity);
        });
        return charityOptional.isPresent();
    }

    public List<DonationDto> getDonations() {
        return donationRepository.findAll().stream()
            .map(this::convert)
            .collect(Collectors.toList());
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
    public DonationDto addDonation(String paypalOrderId, UUID donorId, Long charityId, boolean volunteerOptIn) {
        log.info("Processing PayPal donation with order ID: {}, donor: {}, charity: {}", paypalOrderId, donorId, charityId);

        try {
            // Get PayPal HTTP client and order details
            PayPalHttpClient client = payPalConfig.getPayPalClient();
            HttpResponse<com.paypal.orders.Order> response = client.execute(new com.paypal.orders.OrdersGetRequest(paypalOrderId));
            com.paypal.orders.Order order = response.result();

            if (!"COMPLETED".equals(order.status())) {
                throw new IllegalStateException("Payment not completed. Status: " + order.status());
            }

            PaymentDetails details = extractPaymentDetails(order);
            UserProfile donor = userProfileRepository.findById(donorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Donor not found with ID: " + donorId));
            Charity charity = charityRepository.findById(charityId)
                    .orElseThrow(() -> new ResourceNotFoundException("Charity not found with ID: " + charityId));

            Donation donation = Donation.builder()
                    .paypalId(paypalOrderId)
                    .amount(details.getAmount())
                    .email(details.getEmail())
                    .status(DonationStatus.COMPLETED)
                    .paymentDate(new Date())
                    .currencyCode(details.getCurrencyCode())
                    .volunteerOptIn(volunteerOptIn)
                    .donor(donor)
                    .charity(charity)
                    .build();

            Donation savedDonation = donationRepository.save(donation);

            if (volunteerOptIn) {
                VolunteerVerification verification = VolunteerVerification.builder()
                        .volunteer(donor)
                        .charity(charity)
                        .status(VerificationStatus.PENDING)
                        .build();
                volunteerVerificationRepository.save(verification);
            }

            return convert(savedDonation);
        } catch (Exception e) {
            log.error("Error processing donation: {}", e.getMessage(), e);
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, "Invalid donation data: " + e.getMessage());
        }
    }

    @Transactional
    public VolunteerVerificationDto submitVerification(Long verificationId, String comments, String status) {
        VolunteerVerification verification = volunteerVerificationRepository.findById(verificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Verification not found with ID: " + verificationId));

        VerificationStatus newStatus = VerificationStatus.valueOf(status.toUpperCase());
        verification.setStatus(newStatus);
        verification.setComments(comments);
        verification.setSubmittedAt(new Date());

        VolunteerVerification updated = volunteerVerificationRepository.save(verification);
        return convertVerification(updated);
    }

    private VolunteerVerificationDto convertVerification(VolunteerVerification verification) {
        return VolunteerVerificationDto.builder()
                .id(verification.getId())
                .volunteerId(verification.getVolunteer().getUserProfileid())
                .charityId(verification.getCharity().getId())
                .status(verification.getStatus().name())
                .comments(verification.getComments())
                .submittedAt(verification.getSubmittedAt())
                .build();
    }

}