package com.webapp.realtimeauctionbackend.DTOs;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for bid response data.
 * This class is immutable to ensure thread safety and data consistency.
 */
public final class BidResponseDto {
    private final Long id;
    private final Long auctionId;
    private final Long bidderId;
    private final Double amount;
    private final LocalDateTime timestamp;
    private final BidderDto bidder;

    private BidResponseDto(Builder builder) {
        this.id = builder.id;
        this.auctionId = builder.auctionId;
        this.bidderId = builder.bidderId;
        this.amount = builder.amount;
        this.timestamp = builder.timestamp;
        this.bidder = builder.bidder;
    }

    public Long getId() {
        return id;
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public Long getBidderId() {
        return bidderId;
    }

    public Double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public BidderDto getBidder() {
        return bidder;
    }

    public static class Builder {
        @NotNull(message = "ID is required")
        private Long id;
        @NotNull(message = "Auction ID is required")
        private Long auctionId;
        @NotNull(message = "Bidder ID is required")
        private Long bidderId;
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        private Double amount;
        @NotNull(message = "Timestamp is required")
        private LocalDateTime timestamp;
        @NotNull(message = "Bidder is required")
        private BidderDto bidder;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder auctionId(Long auctionId) {
            this.auctionId = auctionId;
            return this;
        }

        public Builder bidderId(Long bidderId) {
            this.bidderId = bidderId;
            return this;
        }

        public Builder amount(Double amount) {
            this.amount = amount;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder bidder(BidderDto bidder) {
            this.bidder = bidder;
            return this;
        }

        public BidResponseDto build() {
            return new BidResponseDto(this);
        }
    }
}