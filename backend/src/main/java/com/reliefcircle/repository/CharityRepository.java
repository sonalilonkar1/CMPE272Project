package com.reliefcircle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reliefcircle.model.Charity;

public interface CharityRepository extends JpaRepository<Charity, Long> {

}
