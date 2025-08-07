package com.webapp.realtimeauctionbackend.mappers;

import com.webapp.realtimeauctionbackend.DTOs.AuctionResponseDto;
import com.webapp.realtimeauctionbackend.DTOs.BidResponseDto;
import com.webapp.realtimeauctionbackend.DTOs.BidderDto;
import com.webapp.realtimeauctionbackend.DTOs.SellerDto;
import com.webapp.realtimeauctionbackend.models.Auction;
import com.webapp.realtimeauctionbackend.models.Bid;
import com.webapp.realtimeauctionbackend.models.Person;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuctionMapper {
    public AuctionResponseDto auctionToResponseDto(Auction auction) {
        return new AuctionResponseDto.Builder()
                .id(auction.getId())
                .title(auction.getTitle())
                .description(auction.getDescription())
                .startingPrice(auction.getStartingPrice())
                .currentPrice(auction.getCurrentPrice())
                .status(auction.getStatus())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .seller(mapSeller(auction.getSeller()))
                .bids(mapBids(auction.getBids()))
                .build();
    }

    private SellerDto mapSeller(Person seller) {
        return new SellerDto.Builder()
                .id(seller.getId())
                .name(seller.getName())
                .email(seller.getEmail())
                .build();
    }

    private List<BidResponseDto> mapBids(List<Bid> bids) {
        if (bids == null) {
            return Collections.emptyList(); // Return empty list if bids is null
        }
        return bids.stream()
                .sorted(Comparator.comparing(Bid::getTimestamp).reversed())
                .map(this::mapBid)
                .collect(Collectors.toList());
    }

    private BidResponseDto mapBid(Bid bid) {
        return new BidResponseDto.Builder()
                .id(bid.getId())
                .amount(bid.getAmount())
                .timestamp(bid.getTimestamp())
                .bidder(mapBidder(bid.getBidder()))
                .build();
    }

    private BidderDto mapBidder(Person bidder) {
        return new BidderDto.Builder()
                .id(bidder.getId())
                .username(bidder.getName())
                .build();
    }
}