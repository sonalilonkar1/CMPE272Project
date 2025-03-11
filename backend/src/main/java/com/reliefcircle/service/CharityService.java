package com.reliefcircle.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import com.paypal.orders.Order;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.http.exceptions.HttpException;
import com.paypal.orders.OrdersGetRequest;
import com.reliefcircle.config.PayPalConfig;
import com.reliefcircle.dto.CharityDto;
import com.reliefcircle.dto.DonationDto;
import com.reliefcircle.model.Charity;
import com.reliefcircle.model.Donation;
import com.reliefcircle.model.DonationStatus;
import com.reliefcircle.paypal.PaymentDetails;
import com.reliefcircle.repository.CharityRepository;
import com.reliefcircle.repository.DonationRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CharityService {

    private final CharityRepository charityRepository;
    private final DonationRepository donationRepository;
    private final AWSService awsService;
    private final PayPalConfig payPalConfig;

    @Autowired
    public CharityService(CharityRepository charityRepository, 
                          DonationRepository donationRepository, 
                          AWSService awsService,
                          PayPalConfig payPalConfig) {
        this.charityRepository = charityRepository;
        this.donationRepository = donationRepository;
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
     * Process a PayPal donation and save it to the database
     * 
     * @param paypalOrderId The PayPal order ID to verify and process
     * @return The saved donation as a DonationDto
     * @throws HttpServerErrorException if the donation data is invalid or cannot be processed
     */
    @Transactional
    public DonationDto addDonation(String paypalOrderId) {
        log.info("Processing PayPal donation with order ID: {}", paypalOrderId);

        try {
            // Get PayPal HTTP client
            PayPalHttpClient client = payPalConfig.getPayPalClient();
            
            // Create order get request
            OrdersGetRequest request = new OrdersGetRequest(paypalOrderId);
            
            // Call API to get order details
            HttpResponse<Order> response = client.execute(request);
            Order order = response.result();
            
            // Validate payment status
            if (!"COMPLETED".equals(order.status())) {
                throw new IllegalStateException("Payment not completed. Status: " + order.status());
            }
            
            // Extract payment details from the order
            PaymentDetails details = extractPaymentDetails(order);
            
            // Create and save donation entity
            Donation donation = Donation.builder()
                .paypalId(paypalOrderId)
                .amount(details.getAmount())
                .email(details.getEmail())
                .status(DonationStatus.COMPLETED)
                .paymentDate(new Date())
                .currencyCode(details.getCurrencyCode())
                .build();
            
            log.info("Saving verified donation: {}", donation);
            Donation savedDonation = donationRepository.save(donation);
            
            return convert(savedDonation);
        } catch (HttpException e) {
            log.error("PayPal API error: {}", e.getMessage(), e);
            throw new HttpServerErrorException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Failed to verify payment with PayPal: " + e.getMessage()
            );
        } catch (IOException e) {
            log.error("IO error while communicating with PayPal: {}", e.getMessage(), e);
            throw new HttpServerErrorException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "IO error while communicating with PayPal: " + e.getMessage()
            );
        } catch (Exception e) {
            log.error("Error processing donation: {}", e.getMessage(), e);
            throw new HttpServerErrorException(
                HttpStatus.BAD_REQUEST,
                "Invalid donation data: " + e.getMessage()
            );
        }
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

}