package com.webapp.realtimeauctionbackend.controllers;

import com.webapp.realtimeauctionbackend.DTOs.BidDto;
import com.webapp.realtimeauctionbackend.DTOs.BidResponseDto;
import com.webapp.realtimeauctionbackend.exceptions.AuctionClosedException;
import com.webapp.realtimeauctionbackend.exceptions.InvalidBidException;
import com.webapp.realtimeauctionbackend.exceptions.ResourceNotFoundException;
import com.webapp.realtimeauctionbackend.services.BidService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class BidWebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(BidWebSocketController.class);
    private final BidService bidService;

    @Autowired
    public BidWebSocketController(BidService bidService) {
        this.bidService = bidService;
    }

    @MessageMapping("/bids.place")
    public BidResponseDto placeBid(
            @Payload @Valid BidDto bidDto,
            SimpMessageHeaderAccessor headerAccessor) {
        try {
            // Get user ID from the WebSocket session
            Long userId = ((com.webapp.realtimeauctionbackend.models.Person) headerAccessor.getUser()).getId();
            
            // Create a new BidDto with the authenticated user's ID
            BidDto bidWithBidderId = new BidDto.Builder()
                    .auctionId(bidDto.getAuctionId())
                    .bidderId(userId)
                    .amount(bidDto.getAmount())
                    .build();
            
            return bidService.placeBid(bidWithBidderId);
        } catch (Exception e) {
            logger.error("Error placing bid via WebSocket: {}", e.getMessage());
            throw e;
        }
    }

    @SubscribeMapping("/auctions/{auctionId}/bids")
    public BidResponseDto subscribeToBids(@Payload Long auctionId) {
        try {
            return bidService.getHighestBid(auctionId);
        } catch (ResourceNotFoundException e) {
            logger.warn("No bids found for auction ID: {}", auctionId);
            return null;
        }
    }

    @MessageExceptionHandler
    public Map<String, String> handleException(Throwable exception) {
        Map<String, String> error = new HashMap<>();
        
        if (exception instanceof AuctionClosedException) {
            error.put("error", "Auction Closed");
            error.put("message", exception.getMessage());
        } else if (exception instanceof InvalidBidException) {
            error.put("error", "Invalid Bid");
            error.put("message", exception.getMessage());
        } else if (exception instanceof ResourceNotFoundException) {
            error.put("error", "Resource Not Found");
            error.put("message", exception.getMessage());
        } else {
            error.put("error", "Internal Server Error");
            error.put("message", "An unexpected error occurred");
        }
        
        return error;
    }
}