package com.webapp.realtimeauctionbackend.services;

import com.webapp.realtimeauctionbackend.DTOs.BidDto;
import com.webapp.realtimeauctionbackend.DTOs.BidResponseDto;
import com.webapp.realtimeauctionbackend.exceptions.AuctionClosedException;
import com.webapp.realtimeauctionbackend.exceptions.InvalidBidException;
import com.webapp.realtimeauctionbackend.exceptions.ResourceNotFoundException;
import com.webapp.realtimeauctionbackend.models.Auction;
import com.webapp.realtimeauctionbackend.models.Bid;
import com.webapp.realtimeauctionbackend.models.Person;
import com.webapp.realtimeauctionbackend.repositories.AuctionRepository;
import com.webapp.realtimeauctionbackend.repositories.BidRepository;
import com.webapp.realtimeauctionbackend.repositories.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BidService {
    private static final Logger logger = LoggerFactory.getLogger(BidService.class);

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final PersonRepository personRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public BidService(BidRepository bidRepository,
                     AuctionRepository auctionRepository,
                     PersonRepository personRepository,
                     SimpMessagingTemplate messagingTemplate) {
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
        this.personRepository = personRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public BidResponseDto placeBid(BidDto bidDto) {
        logger.info("Processing bid for auction ID: {} from bidder ID: {}", bidDto.getAuctionId(), bidDto.getBidderId());

        // Validate auction exists
        Auction auction = auctionRepository.findById(bidDto.getAuctionId())
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + bidDto.getAuctionId()));

        // Validate bidder exists
        Person bidder = personRepository.findById(bidDto.getBidderId())
                .orElseThrow(() -> new ResourceNotFoundException("Bidder not found with id: " + bidDto.getBidderId()));

        // Check if auction is still active
        if (auction.getEndTime().isBefore(LocalDateTime.now())) {
            logger.error("Auction has ended");
            throw new AuctionClosedException("Auction has ended");
        }

        // Get current highest bid
        Bid currentHighestBid = bidRepository.findFirstByAuctionOrderByAmountDesc(auction)
                .orElse(null);

        // Validate bid amount
        if (currentHighestBid != null && bidDto.getAmount() <= currentHighestBid.getAmount()) {
            logger.error("Bid amount must be higher than current highest bid: {}", currentHighestBid.getAmount());
            throw new InvalidBidException("Bid amount must be higher than current highest bid: " + currentHighestBid.getAmount());
        }

        if (bidDto.getAmount() < auction.getStartingPrice()) {
            logger.error("Bid amount must be at least the starting price: {}", auction.getStartingPrice());
            throw new InvalidBidException("Bid amount must be at least the starting price: " + auction.getStartingPrice());
        }

        // Validate bidder is not the current highest bidder
        if (currentHighestBid != null && currentHighestBid.getBidder().getId().equals(bidDto.getBidderId())) {
            logger.error("You are already the highest bidder");
            throw new InvalidBidException("You are already the highest bidder");
        }

        // Create and save the bid
        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setBidder(bidder);
        bid.setAmount(bidDto.getAmount());
        bid.setTimestamp(LocalDateTime.now());
        bid = bidRepository.save(bid);

        // Update auction's current bidder and price
        auction.setCurrentBidder(bidder);
        auction.setCurrentPrice(bidDto.getAmount());
        auctionRepository.save(auction);

        // Create response DTO
        BidResponseDto responseDto = new BidResponseDto.Builder()
                .id(bid.getId())
                .auctionId(bid.getAuction().getId())
                .bidderId(bid.getBidder().getId())
                .amount(bid.getAmount())
                .timestamp(bid.getTimestamp())
                .build();

        // Send real-time update
        messagingTemplate.convertAndSend("/topic/auctions/" + auction.getId() + "/bids", responseDto);

        logger.info("Bid placed successfully - Auction ID: {}, Bid ID: {}, Amount: {}", 
            auction.getId(), bid.getId(), bid.getAmount());

        return responseDto;
    }

    @Transactional(readOnly = true)
    public BidResponseDto getHighestBid(Long auctionId) {
        logger.info("Fetching highest bid for auction ID: {}", auctionId);
        
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + auctionId));

        Bid highestBid = bidRepository.findFirstByAuctionOrderByAmountDesc(auction)
                .orElseThrow(() -> new ResourceNotFoundException("No bids found for auction: " + auctionId));

        return new BidResponseDto.Builder()
                .id(highestBid.getId())
                .auctionId(highestBid.getAuction().getId())
                .bidderId(highestBid.getBidder().getId())
                .amount(highestBid.getAmount())
                .timestamp(highestBid.getTimestamp())
                .build();
    }

    @Transactional(readOnly = true)
    public List<BidResponseDto> getBidHistory(Long auctionId) {
        logger.info("Fetching bid history for auction ID: {}", auctionId);
        
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + auctionId));

        return bidRepository.findByAuctionOrderByAmountDesc(auction).stream()
                .map(bid -> new BidResponseDto.Builder()
                        .id(bid.getId())
                        .auctionId(bid.getAuction().getId())
                        .bidderId(bid.getBidder().getId())
                        .amount(bid.getAmount())
                        .timestamp(bid.getTimestamp())
                        .build())
                .collect(Collectors.toList());
    }
}