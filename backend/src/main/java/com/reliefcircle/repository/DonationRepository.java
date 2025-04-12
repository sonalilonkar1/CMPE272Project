package com.reliefcircle.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reliefcircle.model.Donation;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByDonorId(UUID donorId);
}
