package com.webapp.realtimeauctionbackend.controllers;

import com.webapp.realtimeauctionbackend.models.Auction;
import com.webapp.realtimeauctionbackend.models.Watchlist;
import com.webapp.realtimeauctionbackend.services.WatchlistService;
import com.webapp.realtimeauctionbackend.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchlistController {
    private final WatchlistService watchlistService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Watchlist> getUserWatchlist() {
        Long userId = SecurityUtils.getCurrentUserId();
        return watchlistService.getUserWatchlist(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/auction/{auctionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Watchlist> addAuctionToWatchlist(@PathVariable Long auctionId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(watchlistService.addAuctionToWatchlist(userId, auctionId));
    }

    @DeleteMapping("/auction/{auctionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeAuctionFromWatchlist(@PathVariable Long auctionId) {
        Long userId = SecurityUtils.getCurrentUserId();
        watchlistService.removeAuctionFromWatchlist(userId, auctionId);
        return ResponseEntity.ok().build();
    }
} 