package com.reliefcircle.repository;

import com.reliefcircle.model.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {
    List<Verification> findByVolunteerId(UUID volunteerId);
    List<Verification> findByCharityId(Long charityId);
}