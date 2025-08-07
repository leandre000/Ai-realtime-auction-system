package com.webapp.realtimeauctionbackend.mappers;

import com.webapp.realtimeauctionbackend.DTOs.BidResponseDto;
import com.webapp.realtimeauctionbackend.DTOs.BidderDto;
import com.webapp.realtimeauctionbackend.models.Bid;
import com.webapp.realtimeauctionbackend.models.Person;
import org.springframework.stereotype.Component;

@Component
public class BidMapper {
    public BidResponseDto bidToResponseDto(Bid bid) {
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
