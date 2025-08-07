package com.webapp.realtimeauctionbackend.services;

import com.webapp.realtimeauctionbackend.constants.AuctionStatus;
import com.webapp.realtimeauctionbackend.models.Auction;
import com.webapp.realtimeauctionbackend.repositories.AuctionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuctionStatusService {
    private static final Logger logger = LoggerFactory.getLogger(AuctionStatusService.class);
    private final AuctionRepository auctionRepository;

    @Autowired
    public AuctionStatusService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void updateAuctionStatuses() {
        LocalDateTime now = LocalDateTime.now();
        logger.info("Starting auction status update check at: {}", now);

        // Update scheduled auctions that should be active
        List<Auction> scheduledAuctions = auctionRepository.findByStatusAndStartTimeBefore(
                AuctionStatus.SCHEDULED, now);
        for (Auction auction : scheduledAuctions) {
            auction.setStatus(AuctionStatus.ACTIVE);
            logger.info("Updated auction ID: {} from SCHEDULED to ACTIVE", auction.getId());
        }

        // Update active auctions that should be closed
        List<Auction> activeAuctions = auctionRepository.findByStatusAndEndTimeBefore(
                AuctionStatus.ACTIVE, now);
        for (Auction auction : activeAuctions) {
            auction.setStatus(AuctionStatus.CLOSED);
            logger.info("Updated auction ID: {} from ACTIVE to CLOSED", auction.getId());
        }

        // Save all changes
        if (!scheduledAuctions.isEmpty() || !activeAuctions.isEmpty()) {
            auctionRepository.saveAll(scheduledAuctions);
            auctionRepository.saveAll(activeAuctions);
            logger.info("Updated {} auctions from SCHEDULED to ACTIVE and {} auctions from ACTIVE to CLOSED",
                    scheduledAuctions.size(), activeAuctions.size());
        }
    }

    public void validateAuctionStatus(Auction auction) {
        LocalDateTime now = LocalDateTime.now();
        
        if (auction.getStatus() == AuctionStatus.CLOSED) {
            throw new IllegalStateException("Cannot modify a closed auction");
        }
        
        if (auction.getStatus() == AuctionStatus.ACTIVE && now.isAfter(auction.getEndTime())) {
            auction.setStatus(AuctionStatus.CLOSED);
            logger.info("Auction ID: {} automatically closed as end time has passed", auction.getId());
        }
        
        if (auction.getStatus() == AuctionStatus.SCHEDULED && now.isAfter(auction.getStartTime())) {
            auction.setStatus(AuctionStatus.ACTIVE);
            logger.info("Auction ID: {} automatically activated as start time has passed", auction.getId());
        }
    }
} 