package com.webapp.realtimeauctionbackend.repositories;

import com.webapp.realtimeauctionbackend.constants.AuctionStatus;
import com.webapp.realtimeauctionbackend.models.Auction;
import com.webapp.realtimeauctionbackend.models.Person;
import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Auction a WHERE a.id = :id")
    Optional<Auction> findAuctionById(@Param("id") Long id);

    @Query("SELECT a FROM Auction a WHERE a.status = :status")
    Page<Auction> findByStatus(@Param("status") AuctionStatus status, Pageable pageable);

    @Query("SELECT a FROM Auction a WHERE a.status = :status AND a.startTime < :startTimeBefore")
    List<Auction> findByStatusAndStartTimeBefore(
            @Param("status") AuctionStatus status,
            @NotNull(message = "Start time is required") @Param("startTimeBefore") LocalDateTime startTimeBefore);

    @Query("SELECT a FROM Auction a WHERE a.status = :status AND a.endTime < :endTimeBefore")
    List<Auction> findByStatusAndEndTimeBefore(
            @Param("status") AuctionStatus status,
            @NotNull(message = "End time is required") @Param("endTimeBefore") LocalDateTime endTimeBefore);

    @Query("SELECT a FROM Auction a WHERE a.seller.id = :sellerId")
    Page<Auction> findBySellerId(@Param("sellerId") Long sellerId, Pageable pageable);

    @Query("SELECT a FROM Auction a WHERE a.status = :status AND a.seller.id = :sellerId")
    Page<Auction> findByStatusAndSellerId(
            @Param("status") AuctionStatus status,
            @Param("sellerId") Long sellerId,
            Pageable pageable);

    @Query("SELECT a FROM Auction a WHERE a.seller = :seller")
    Page<Auction> findBySeller(@Param("seller") Person seller, Pageable pageable);

    @Query("SELECT a FROM Auction a WHERE a.currentBidder = :currentBidder AND a.status = :status")
    Page<Auction> findByCurrentBidderAndStatus(
            @Param("currentBidder") Person currentBidder,
            @Param("status") AuctionStatus status,
            Pageable pageable);

    @Query("SELECT COUNT(a) FROM Auction a WHERE a.status = :status")
    long countByStatus(@Param("status") AuctionStatus status);

    @Query("SELECT COUNT(a) FROM Auction a WHERE a.createdAt < :date")
    long countByCreatedAtBefore(@Param("date") LocalDateTime date);

    @Query("SELECT COUNT(a) FROM Auction a WHERE a.createdAt BETWEEN :start AND :end")
    long countByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT a FROM Auction a WHERE a.status = :status")
    List<Auction> findByStatus(@Param("status") AuctionStatus status);
}