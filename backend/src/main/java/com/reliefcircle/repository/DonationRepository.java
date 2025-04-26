package com.reliefcircle.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.reliefcircle.model.Donation;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByDonorId(UUID donorId);
    Page<Donation> findByDonorId(UUID donorId, Pageable pageable);
    Donation findByPaypalOrderId(String paypalOrderId);
}
