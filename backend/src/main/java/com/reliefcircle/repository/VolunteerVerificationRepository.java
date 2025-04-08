package com.reliefcircle.repository;

import com.reliefcircle.model.VolunteerVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VolunteerVerificationRepository extends JpaRepository<VolunteerVerification, Long> {
}