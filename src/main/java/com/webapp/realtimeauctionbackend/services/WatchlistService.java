package com.webapp.realtimeauctionbackend.services;

import com.webapp.realtimeauctionbackend.models.Auction;
import com.webapp.realtimeauctionbackend.models.Person;
import com.webapp.realtimeauctionbackend.models.Watchlist;
import com.webapp.realtimeauctionbackend.repositories.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WatchlistService {
    private final WatchlistRepository watchlistRepository;

    public Optional<Watchlist> getUserWatchlist(Long userId) {
        return watchlistRepository.findByUserId(userId);
    }

    @Transactional
    public Watchlist addAuctionToWatchlist(Long userId, Long auctionId) {
        Watchlist watchlist = watchlistRepository.findByUserId(userId)
            .orElseGet(() -> Watchlist.builder()
                .user(Person.builder().id(userId).build())
                .build());

        if (!watchlist.getAuctions().stream().anyMatch(auction -> auction.getId().equals(auctionId))) {
            Auction auction = new Auction();
            auction.setId(auctionId);
            watchlist.getAuctions().add(auction);
        }

        return watchlistRepository.save(watchlist);
    }

    @Transactional
    public void removeAuctionFromWatchlist(Long userId, Long auctionId) {
        watchlistRepository.findByUserIdAndAuctionId(userId, auctionId)
            .ifPresent(watchlist -> {
                watchlist.getAuctions().removeIf(auction -> auction.getId().equals(auctionId));
                watchlistRepository.save(watchlist);
            });
    }
} 