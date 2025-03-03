package com.reliefcircle.datastore;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reliefcircle.model.Donation;

public interface DonationRepository extends JpaRepository<Donation, Long> {

}
