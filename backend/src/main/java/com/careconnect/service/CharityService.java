package com.careconnect.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.careconnect.datastore.CharityRepository;
import com.careconnect.datastore.DonationRepository;
import com.careconnect.dto.CharityDto;
import com.careconnect.dto.DonationDto;
import com.careconnect.model.Charity;
import com.careconnect.model.Donation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CharityService {

    private final CharityRepository charityRepository;
    private final DonationRepository donationRepository;
    private final AWSService awsService;

    public CharityService(CharityRepository charityRepository, 
                          DonationRepository donationRepository, 
                          AWSService awsService) {
        this.charityRepository = charityRepository;
        this.donationRepository = donationRepository;
        this.awsService = awsService;
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

    public boolean addDonation(String paypal) {
        log.info("Received PayPal donation: {}", paypal);

        JsonObject convertedObject = new Gson().fromJson(paypal, JsonObject.class);
        Donation donation = Donation.builder()
            .paypalId(convertedObject.get("id").getAsString())
            .amount(convertedObject.get("purchase_units").getAsJsonArray().get(0)
                    .getAsJsonObject().get("amount").getAsJsonObject().get("value").getAsDouble())
            .email(convertedObject.get("payer").getAsJsonObject().get("email_address").getAsString())
            .build();

        log.info("Saving donation: {}", donation);
        donationRepository.save(donation);
        return true;
    }
}
