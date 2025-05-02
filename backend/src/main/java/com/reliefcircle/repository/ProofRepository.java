package com.reliefcircle.repository;

import com.reliefcircle.model.Proof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProofRepository extends JpaRepository<Proof, Long> {
} 