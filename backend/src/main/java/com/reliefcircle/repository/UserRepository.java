package com.reliefcircle.repository;

import com.reliefcircle.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByExternalId(String externalId);
    Optional<User> findByEmail(String email);
    List<User> findByRoleAndIsVolunteerTrue(User.UserRole role);
    Page<User> findByRoleAndIsVolunteerTrue(User.UserRole role, Pageable pageable);
}