package com.reliefcircle.repository;

import com.reliefcircle.model.Update;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UpdateRepository extends JpaRepository<Update, Long> {

    /**
     * Find all updates for a specific charity
     */
    Page<Update> findByCharityIdOrderByCreatedAtDesc(Long charityId, Pageable pageable);

    /**
     * Find all updates created by a specific fundraiser
     */
    Page<Update> findByFundraiserIdOrderByCreatedAtDesc(UUID fundraiserId, Pageable pageable);

    /**
     * Find all updates for charities that a donor has donated to
     */
    @Query("SELECT DISTINCT u FROM Update u JOIN u.charity c JOIN c.donations d " +
           "WHERE d.donor.id = :donorId ORDER BY u.createdAt DESC")
    Page<Update> findByDonorIdOrderByCreatedAtDesc(@Param("donorId") UUID donorId, Pageable pageable);
    
    /**
     * Find all updates for charities verified by a volunteer
     */
    @Query("SELECT DISTINCT u FROM Update u JOIN u.charity c " +
           "WHERE c.isVerified = true AND EXISTS (SELECT v FROM Verification v WHERE v.charity = c AND v.volunteer.id = :volunteerId) " +
           "ORDER BY u.createdAt DESC")
    Page<Update> findByVolunteerIdOrderByCreatedAtDesc(@Param("volunteerId") UUID volunteerId, Pageable pageable);

    /**
     * Find top rated updates
     */
    Page<Update> findByAverageRatingGreaterThanEqualOrderByAverageRatingDesc(Double minRating, Pageable pageable);

    /**
     * Find recent updates
     */
    Page<Update> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Count updates by charity
     */
    Long countByCharityId(Long charityId);

    /**
     * Count updates by fundraiser
     */
    Long countByFundraiserId(UUID fundraiserId);
}