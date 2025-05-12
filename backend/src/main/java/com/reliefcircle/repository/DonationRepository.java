package com.reliefcircle.repository;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reliefcircle.model.Donation;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByDonorId(UUID donorId);
    Page<Donation> findByDonorId(UUID donorId, Pageable pageable);
    Donation findByStripeOrderId(String stripeOrderId);

    @Query("SELECT COUNT(DISTINCT d.donor.id) FROM Donation d " +
           "JOIN d.charity c WHERE c.fundraiser.id = :fundraiserId")
    long countUniqueDonorsByFundraiserId(@Param("fundraiserId") UUID fundraiserId);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.donor.id = :donorId")
    Optional<BigDecimal> sumAmountByDonorId(@Param("donorId") UUID donorId);

    @Query("SELECT COUNT(DISTINCT d.charity.id) FROM Donation d WHERE d.donor.id = :donorId")
    long countUniqueCharitiesByDonorId(@Param("donorId") UUID donorId);
}
