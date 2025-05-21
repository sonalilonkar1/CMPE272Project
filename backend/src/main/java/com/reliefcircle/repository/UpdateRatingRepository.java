package com.reliefcircle.repository;

import com.reliefcircle.model.UpdateRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UpdateRatingRepository extends JpaRepository<UpdateRating, Long> {

    /**
     * Find all ratings for a specific update
     */
    List<UpdateRating> findByUpdateId(Long updateId);

    /**
     * Find paginated ratings for a specific update
     */
    Page<UpdateRating> findByUpdateId(Long updateId, Pageable pageable);

    /**
     * Find a specific rating by update and donor
     */
    Optional<UpdateRating> findByUpdateIdAndDonorId(Long updateId, UUID donorId);

    /**
     * Find all ratings submitted by a specific donor
     */
    List<UpdateRating> findByDonorId(UUID donorId);

    

    /**
     * Find paginated ratings submitted by a specific donor
     */
    Page<UpdateRating> findByDonorId(UUID donorId, Pageable pageable);

    /**
     * Check if donor has already rated an update
     */
    boolean existsByUpdateIdAndDonorId(Long updateId, UUID donorId);

    /**
     * Get average rating for an update
     */
    @Query("SELECT AVG(r.rating) FROM UpdateRating r WHERE r.update.id = :updateId")
    Double getAverageRatingForUpdate(@Param("updateId") Long updateId);

    /**
     * Count the number of ratings for an update
     */
    Long countByUpdateId(Long updateId);

    /**
     * Find highest rated updates by calculating average
     */
    @Query("SELECT r.update.id, AVG(r.rating) as avgRating " +
           "FROM UpdateRating r " +
           "GROUP BY r.update.id " +
           "ORDER BY avgRating DESC")
    Page<Object[]> findHighestRatedUpdates(Pageable pageable);

    @Query("SELECT COUNT(ur) > 0 FROM UpdateRating ur " +
           "WHERE ur.update.charity.id = :charityId AND ur.donor.id = :volunteerId")
    boolean hasVolunteerRatedAnyUpdateForCharity(@Param("charityId") Long charityId, 
                                                @Param("volunteerId") UUID volunteerId);
}