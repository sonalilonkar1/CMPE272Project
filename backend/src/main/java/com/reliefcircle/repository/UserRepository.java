package com.reliefcircle.repository;

import com.reliefcircle.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    @Query(value = """
        SELECT u.*
        FROM users u
        WHERE u.role = 'VOLUNTEER'
          AND u.id NOT IN (
              SELECT d.user_id
              FROM donations d
              WHERE d.charity_id = :charityId
          )
        ORDER BY RANDOM()
        LIMIT :limit
    """, nativeQuery = true)
    List<User> findRandomVolunteersNotDonatedToCharity(@Param("charityId") Long charityId, @Param("limit") int limit);

}