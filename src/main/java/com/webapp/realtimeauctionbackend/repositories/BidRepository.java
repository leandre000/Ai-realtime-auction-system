package com.webapp.realtimeauctionbackend.repositories;

import com.webapp.realtimeauctionbackend.models.Auction;
import com.webapp.realtimeauctionbackend.models.Bid;
import com.webapp.realtimeauctionbackend.models.Person;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Bid b WHERE b.id = :id")
    Bid findBidById(@Param("id") Long id);

    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId ORDER BY b.amount DESC")
    List<Bid> findBidsByAuctionId(@Param("auctionId") Long auctionId);

    @Query("SELECT b FROM Bid b WHERE b.bidder.id = :bidderId")
    Page<Bid> findBidsByBidderId(@Param("bidderId") Long bidderId, Pageable pageable);

    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId AND b.amount = (SELECT MAX(b2.amount) FROM Bid b2 WHERE b2.auction.id = :auctionId)")
    Bid findHighestBidForAuction(@Param("auctionId") Long auctionId);

    @Query("SELECT COUNT(b) FROM Bid b WHERE b.auction.id = :auctionId")
    long countBidsForAuction(@Param("auctionId") Long auctionId);

    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId ORDER BY b.amount DESC")
    Optional<Bid> findTopByAuctionIdOrderByAmountDesc(@Param("auctionId") Long auctionId);

    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId ORDER BY b.timestamp DESC")
    List<Bid> findByAuctionIdOrderByTimestampDesc(@Param("auctionId") Long auctionId);

    Optional<Bid> findFirstByAuctionOrderByAmountDesc(Auction auction);
    List<Bid> findByAuctionOrderByAmountDesc(Auction auction);

    @Query("SELECT b FROM Bid b WHERE b.bidder = :bidder")
    Page<Bid> findByBidder(@Param("bidder") Person bidder, Pageable pageable);

    @Query("SELECT COUNT(b) FROM Bid b WHERE b.timestamp > :date")
    long countByCreatedAtAfter(@Param("date") LocalDateTime date);

    @Query("SELECT COUNT(b) FROM Bid b WHERE b.timestamp < :date")
    long countByCreatedAtBefore(@Param("date") LocalDateTime date);

    @Query("SELECT COUNT(b) FROM Bid b WHERE b.timestamp BETWEEN :start AND :end")
    long countByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}