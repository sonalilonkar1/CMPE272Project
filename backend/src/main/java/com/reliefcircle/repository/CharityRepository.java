package com.reliefcircle.repository;

import com.reliefcircle.model.Charity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CharityRepository extends JpaRepository<Charity, Long> {
    List<Charity> findByFundraiserId(UUID fundraiserId);
    Page<Charity> findByFundraiserId(UUID fundraiserId, Pageable pageable);
    Page<Charity> findByIsVerified(boolean isVerified, Pageable pageable);
    @Query("SELECT DISTINCT c FROM Charity c JOIN c.donations d WHERE d.donor.id = :donorId")
    Page<Charity> findDistinctByDonorId(@Param("donorId") UUID donorId, Pageable pageable);
}
