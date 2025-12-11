package com.smartmes.repository;

import com.smartmes.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * User repository for database operations
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Update last login time
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLoginTime = :loginTime, u.loginAttempts = 0, u.lockedUntil = null WHERE u.userId = :userId")
    void updateLoginSuccess(@Param("userId") String userId, @Param("loginTime") LocalDateTime loginTime);

    /**
     * Increment login attempts
     */
    @Modifying
    @Query("UPDATE User u SET u.loginAttempts = u.loginAttempts + 1 WHERE u.userId = :userId")
    void incrementLoginAttempts(@Param("userId") String userId);

    /**
     * Lock user account until specified time
     */
    @Modifying
    @Query("UPDATE User u SET u.lockedUntil = :lockedUntil WHERE u.userId = :userId")
    void lockAccount(@Param("userId") String userId, @Param("lockedUntil") LocalDateTime lockedUntil);

    /**
     * Unlock user account
     */
    @Modifying
    @Query("UPDATE User u SET u.loginAttempts = 0, u.lockedUntil = null WHERE u.userId = :userId")
    void unlockAccount(@Param("userId") String userId);
}
