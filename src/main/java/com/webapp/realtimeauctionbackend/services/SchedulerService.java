package com.webapp.realtimeauctionbackend.services;

import com.webapp.realtimeauctionbackend.models.Auction;
import com.webapp.realtimeauctionbackend.constants.AuctionStatus;
import com.webapp.realtimeauctionbackend.models.Bid;
import com.webapp.realtimeauctionbackend.repositories.AuctionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class SchedulerService {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    private final AuctionRepository auctionRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public SchedulerService(AuctionRepository auctionRepository, SimpMessagingTemplate messagingTemplate) {
        this.auctionRepository = auctionRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateAuctionStatuses() {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // Update SCHEDULED to ACTIVE
            List<Auction> scheduledAuctions = auctionRepository.findByStatusAndStartTimeBefore(AuctionStatus.SCHEDULED, now);
            
            for (Auction auction : scheduledAuctions) {
                auction.setStatus(AuctionStatus.ACTIVE);
                auctionRepository.save(auction);
                notifyAuctionStatusChange(auction, "ACTIVE");
                logger.info("Auction {} status changed to ACTIVE", auction.getId());
            }

            // Update ACTIVE to CLOSED
            List<Auction> activeAuctions = auctionRepository.findByStatusAndEndTimeBefore(
                    AuctionStatus.ACTIVE, now);
            
            for (Auction auction : activeAuctions) {
                auction.setStatus(AuctionStatus.CLOSED);
                auctionRepository.save(auction);
                notifyAuctionStatusChange(auction, "CLOSED");
                notifyWinner(auction);
                logger.info("Auction {} status changed to CLOSED", auction.getId());
            }
        } catch (Exception e) {
            logger.error("Error updating auction statuses: {}", e.getMessage(), e);
        }
    }

    private void notifyAuctionStatusChange(Auction auction, String newStatus) {
        messagingTemplate.convertAndSend(
                "/topic/auction/" + auction.getId() + "/status",
                newStatus
        );
    }

    private void notifyWinner(Auction auction) {
        if (!auction.getBids().isEmpty()) {
            auction.getBids().stream()
                    .max(Comparator.comparingDouble(Bid::getAmount))
                    .ifPresent(winningBid -> {
                        messagingTemplate.convertAndSendToUser(
                                winningBid.getBidder().getEmail(),
                                "/queue/auction-win",
                                String.format("Congratulations! You won the auction '%s' with a bid of $%.2f",
                                        auction.getTitle(),
                                        winningBid.getAmount())
                        );
                        logger.info("Winner notification sent for auction {}", auction.getId());
                    });
        }
    }
}