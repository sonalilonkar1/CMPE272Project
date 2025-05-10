package com.reliefcircle.config;

import com.reliefcircle.model.User;
import com.reliefcircle.model.User.UserRole;
import com.reliefcircle.model.Charity;
import com.reliefcircle.model.Donation;
import com.reliefcircle.model.Proof;
import com.reliefcircle.model.Verification;
import com.reliefcircle.model.Verification.VerificationStatus;
import com.reliefcircle.repository.UserRepository;
import com.reliefcircle.repository.CharityRepository;
import com.reliefcircle.repository.DonationRepository;
import com.reliefcircle.repository.ProofRepository;
import com.reliefcircle.repository.VerificationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Configuration
@Profile("!test")
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            UserRepository userRepository,
            CharityRepository charityRepository,
            DonationRepository donationRepository,
            ProofRepository proofRepository,
            VerificationRepository verificationRepository) {
        return args -> {
            // Check if data already exists
            if (userRepository.count() > 0) {
                System.out.println("Data already exists. Skipping initialization.");
                return;
            }

            System.out.println("No existing data found. Initializing new data...");
            
            // Create authenticated user first
            User authenticatedUser = User.builder()
                .id(UUID.fromString("a1b7766b-7dee-446f-85ff-e6034bb02773"))
                .externalId("112862031507191792532")
                .email("sonali.lonkar@sjsu.edu")
                .fullName("Sonali Lonkar")
                .role(UserRole.DONOR)
                .passwordHash("OAUTH2_USER")
                .build();

            // Save authenticated user first
            authenticatedUser = userRepository.save(authenticatedUser);
            System.out.println("Created authenticated user");

            // Create 5 donors (some of whom are also volunteers)
            List<User> donors = Arrays.asList(
                User.builder()
                    .id(UUID.randomUUID())
                    .email("john.donor@example.com")
                    .fullName("John Donor")
                    .role(UserRole.DONOR)
                    .isVolunteer(true)
                    .passwordHash("password123")
                    .build(),
                User.builder()
                    .id(UUID.randomUUID())
                    .email("sarah.donor@example.com")
                    .fullName("Sarah Donor")
                    .role(UserRole.DONOR)
                    .isVolunteer(true)
                    .passwordHash("password123")
                    .build(),
                User.builder()
                    .id(UUID.randomUUID())
                    .email("mike.donor@example.com")
                    .fullName("Mike Donor")
                    .role(UserRole.DONOR)
                    .isVolunteer(false)
                    .passwordHash("password123")
                    .build(),
                User.builder()
                    .id(UUID.randomUUID())
                    .email("emma.donor@example.com")
                    .fullName("Emma Donor")
                    .role(UserRole.DONOR)
                    .isVolunteer(true)
                    .passwordHash("password123")
                    .build(),
                User.builder()
                    .id(UUID.randomUUID())
                    .email("david.donor@example.com")
                    .fullName("David Donor")
                    .role(UserRole.DONOR)
                    .isVolunteer(false)
                    .passwordHash("password123")
                    .build()
            );

            donors = userRepository.saveAll(donors);
            System.out.println("Created " + donors.size() + " donors");

            // Create 5 fundraisers
            List<User> fundraisers = Arrays.asList(
                User.builder()
                    .id(UUID.randomUUID())
                    .email("alice.fundraiser@example.com")
                    .fullName("Alice Fundraiser")
                    .role(UserRole.FUNDRAISER)
                    .passwordHash("password123")
                    .build(),
                User.builder()
                    .id(UUID.randomUUID())
                    .email("bob.fundraiser@example.com")
                    .fullName("Bob Fundraiser")
                    .role(UserRole.FUNDRAISER)
                    .passwordHash("password123")
                    .build(),
                User.builder()
                    .id(UUID.randomUUID())
                    .email("charlie.fundraiser@example.com")
                    .fullName("Charlie Fundraiser")
                    .role(UserRole.FUNDRAISER)
                    .passwordHash("password123")
                    .build(),
                User.builder()
                    .id(UUID.randomUUID())
                    .email("diana.fundraiser@example.com")
                    .fullName("Diana Fundraiser")
                    .role(UserRole.FUNDRAISER)
                    .passwordHash("password123")
                    .build(),
                User.builder()
                    .id(UUID.randomUUID())
                    .email("edward.fundraiser@example.com")
                    .fullName("Edward Fundraiser")
                    .role(UserRole.FUNDRAISER)
                    .passwordHash("password123")
                    .build()
            );

            fundraisers = userRepository.saveAll(fundraisers);
            System.out.println("Created " + fundraisers.size() + " fundraisers");

            // Create 5 charities
            List<Charity> charities = Arrays.asList(
                Charity.builder()
                    .name("SJSU Student Emergency Fund")
                    .description("Support SJSU students facing financial emergencies")
                    .fundraiser(fundraisers.get(0))
                    .isVerified(true)
                    .targetAmount(new BigDecimal("50000.00"))
                    .raisedAmount(new BigDecimal("25000.00"))
                    .build(),
                Charity.builder()
                    .name("Local Food Bank")
                    .description("Providing meals to families in need")
                    .fundraiser(fundraisers.get(1))
                    .isVerified(true)
                    .targetAmount(new BigDecimal("100000.00"))
                    .raisedAmount(new BigDecimal("75000.00"))
                    .build(),
                Charity.builder()
                    .name("Animal Shelter")
                    .description("Caring for abandoned animals")
                    .fundraiser(fundraisers.get(2))
                    .isVerified(true)
                    .targetAmount(new BigDecimal("30000.00"))
                    .raisedAmount(new BigDecimal("15000.00"))
                    .build(),
                Charity.builder()
                    .name("Education Fund")
                    .description("Supporting underprivileged students")
                    .fundraiser(fundraisers.get(3))
                    .isVerified(true)
                    .targetAmount(new BigDecimal("80000.00"))
                    .raisedAmount(new BigDecimal("40000.00"))
                    .build(),
                Charity.builder()
                    .name("Medical Relief")
                    .description("Providing medical aid to those in need")
                    .fundraiser(fundraisers.get(4))
                    .isVerified(true)
                    .targetAmount(new BigDecimal("120000.00"))
                    .raisedAmount(new BigDecimal("60000.00"))
                    .build()
            );

            charities = charityRepository.saveAll(charities);
            System.out.println("Created " + charities.size() + " charities");

            // Create donations from donors to charities
            List<Donation> donations = Arrays.asList(
                // Donations from authenticated user
                Donation.builder()
                    .donor(authenticatedUser)
                    .charity(charities.get(0))
                    .amount(new BigDecimal("100.00").doubleValue())
                    .status(Donation.DonationStatus.COMPLETED)
                    .paypalOrderId("PAY-123456789")
                    .createdAt(LocalDateTime.now().minusDays(7))
                    .build(),
                Donation.builder()
                    .donor(authenticatedUser)
                    .charity(charities.get(1))
                    .amount(new BigDecimal("50.00").doubleValue())
                    .status(Donation.DonationStatus.COMPLETED)
                    .paypalOrderId("PAY-987654321")
                    .createdAt(LocalDateTime.now().minusDays(3))
                    .build(),
                // Donations from other donors
                Donation.builder()
                    .donor(donors.get(0))
                    .charity(charities.get(2))
                    .amount(new BigDecimal("200.00").doubleValue())
                    .status(Donation.DonationStatus.COMPLETED)
                    .paypalOrderId("PAY-111111111")
                    .createdAt(LocalDateTime.now().minusDays(5))
                    .build(),
                Donation.builder()
                    .donor(donors.get(1))
                    .charity(charities.get(3))
                    .amount(new BigDecimal("150.00").doubleValue())
                    .status(Donation.DonationStatus.COMPLETED)
                    .paypalOrderId("PAY-222222222")
                    .createdAt(LocalDateTime.now().minusDays(4))
                    .build(),
                Donation.builder()
                    .donor(donors.get(2))
                    .charity(charities.get(4))
                    .amount(new BigDecimal("75.00").doubleValue())
                    .status(Donation.DonationStatus.COMPLETED)
                    .paypalOrderId("PAY-333333333")
                    .createdAt(LocalDateTime.now().minusDays(2))
                    .build(),
                Donation.builder()
                    .donor(donors.get(3))
                    .charity(charities.get(0))
                    .amount(new BigDecimal("300.00").doubleValue())
                    .status(Donation.DonationStatus.COMPLETED)
                    .paypalOrderId("PAY-444444444")
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .build(),
                Donation.builder()
                    .donor(donors.get(4))
                    .charity(charities.get(1))
                    .amount(new BigDecimal("125.00").doubleValue())
                    .status(Donation.DonationStatus.COMPLETED)
                    .paypalOrderId("PAY-555555555")
                    .createdAt(LocalDateTime.now().minusDays(6))
                    .build()
            );

            donations = donationRepository.saveAll(donations);
            System.out.println("Created " + donations.size() + " donations");

            // Create proofs for charities
            List<Proof> proofs = Arrays.asList(
                Proof.builder()
                    .charity(charities.get(0))
                    .description("Receipt for food supplies")
                    .fileUrl("https://example.com/proof1.jpg")
                    .uploadedAt(LocalDateTime.now().minusDays(5))
                    .build(),
                Proof.builder()
                    .charity(charities.get(1))
                    .description("Distribution event photos")
                    .fileUrl("https://example.com/proof2.jpg")
                    .uploadedAt(LocalDateTime.now().minusDays(2))
                    .build(),
                Proof.builder()
                    .charity(charities.get(2))
                    .description("Veterinary bills")
                    .fileUrl("https://example.com/proof3.jpg")
                    .uploadedAt(LocalDateTime.now().minusDays(4))
                    .build(),
                Proof.builder()
                    .charity(charities.get(3))
                    .description("School supplies receipts")
                    .fileUrl("https://example.com/proof4.jpg")
                    .uploadedAt(LocalDateTime.now().minusDays(3))
                    .build(),
                Proof.builder()
                    .charity(charities.get(4))
                    .description("Medical equipment invoices")
                    .fileUrl("https://example.com/proof5.jpg")
                    .uploadedAt(LocalDateTime.now().minusDays(1))
                    .build()
            );

            proofs = proofRepository.saveAll(proofs);
            System.out.println("Created " + proofs.size() + " proofs");

            // Create verifications by volunteers
            List<Verification> verifications = Arrays.asList(
                Verification.builder()
                    .volunteer(donors.get(0)) // John (donor and volunteer)
                    .charity(charities.get(0))
                    .proof(proofs.get(0))
                    .status(VerificationStatus.APPROVED)
                    .comment("All documentation verified")
                    .reviewedAt(LocalDateTime.now().minusDays(4))
                    .build(),
                Verification.builder()
                    .volunteer(donors.get(1)) // Sarah (donor and volunteer)
                    .charity(charities.get(1))
                    .proof(proofs.get(1))
                    .status(VerificationStatus.APPROVED)
                    .comment("Distribution verified")
                    .reviewedAt(LocalDateTime.now().minusDays(3))
                    .build(),
                Verification.builder()
                    .volunteer(donors.get(3)) // Emma (donor and volunteer)
                    .charity(charities.get(2))
                    .proof(proofs.get(2))
                    .status(VerificationStatus.APPROVED)
                    .comment("Veterinary services verified")
                    .reviewedAt(LocalDateTime.now().minusDays(2))
                    .build(),
                Verification.builder()
                    .volunteer(donors.get(0)) // John (donor and volunteer)
                    .charity(charities.get(3))
                    .proof(proofs.get(3))
                    .status(VerificationStatus.PENDING)
                    .comment("Pending review")
                    .build(),
                Verification.builder()
                    .volunteer(donors.get(1)) // Sarah (donor and volunteer)
                    .charity(charities.get(4))
                    .proof(proofs.get(4))
                    .status(VerificationStatus.APPROVED)
                    .comment("Medical equipment verified")
                    .reviewedAt(LocalDateTime.now().minusDays(1))
                    .build()
            );

            verifications = verificationRepository.saveAll(verifications);
            System.out.println("Created " + verifications.size() + " verifications");
            
            System.out.println("Data initialization completed successfully");
        };
    }
    
} 