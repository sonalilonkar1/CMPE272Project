package com.reliefcircle.service;

import com.reliefcircle.dto.DonationDto;
import com.reliefcircle.model.Charity;
import com.reliefcircle.model.Donation;
import com.reliefcircle.model.User;
import com.reliefcircle.repository.CharityRepository;
import com.reliefcircle.repository.DonationRepository;
import com.reliefcircle.repository.UserRepository;
import com.reliefcircle.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;
    private final CharityRepository charityRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<DonationDto> getDonationsByDonor(UUID donorId, Pageable pageable) {
        Page<Donation> donations = donationRepository.findByDonorId(donorId, pageable);
        return donations.map(this::convertToDto);
    }

    @Transactional
    public DonationDto createDonation(DonationDto donationDto, UUID donorId) {
        Charity charity = charityRepository.findById(donationDto.getCharityId())
                .orElseThrow(() -> new ResourceNotFoundException("Charity not found"));

        User donor = userRepository.findById(donorId)
                .orElseThrow(() -> new ResourceNotFoundException("Donor not found"));

        Donation donation = Donation.builder()
                .amount(donationDto.getAmount())
                .charity(charity)
                .donor(donor)
                .build();

        return convertToDto(donationRepository.save(donation));
    }

    private DonationDto convertToDto(Donation donation) {
        return DonationDto.builder()
                .id(donation.getId())
                .amount(donation.getAmount())
                .charityId(donation.getCharity().getId())
                .donorId(donation.getDonor().getId())
                .createdAt(donation.getCreatedAt())
                .build();
    }
}