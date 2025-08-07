package com.webapp.realtimeauctionbackend.repositories;

import com.webapp.realtimeauctionbackend.models.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    Optional<Watchlist> findByUserId(Long userId);

    @Query("SELECT w FROM Watchlist w JOIN w.auctions a WHERE w.user.id = :userId AND a.id = :auctionId")
    Optional<Watchlist> findByUserIdAndAuctionId(@Param("userId") Long userId, @Param("auctionId") Long auctionId);
} 