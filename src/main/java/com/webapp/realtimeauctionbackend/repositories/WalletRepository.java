package com.webapp.realtimeauctionbackend.repositories;

import com.webapp.realtimeauctionbackend.constants.WalletStatus;
import com.webapp.realtimeauctionbackend.constants.VerificationLevel;
import com.webapp.realtimeauctionbackend.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId")
    Optional<Wallet> findByUserId(@Param("userId") Long userId);

    @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId AND w.status = :status")
    Optional<Wallet> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") WalletStatus status);

    @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId AND w.verificationLevel = :level")
    Optional<Wallet> findByUserIdAndVerificationLevel(@Param("userId") Long userId, @Param("level") VerificationLevel level);
} 