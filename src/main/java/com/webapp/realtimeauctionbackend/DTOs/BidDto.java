package com.webapp.realtimeauctionbackend.DTOs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for bid data.
 * This class is immutable to ensure thread safety and data consistency.
 */
public final class BidDto {
    @NotNull(message = "Auction ID is required")
    private final Long auctionId;
    private final Long bidderId;
    @NotNull(message = "Bid amount is required")
    @Positive(message = "Bid amount must be greater than 0")
    private final Double amount;

    @JsonCreator
    public BidDto(
            @JsonProperty("auctionId") Long auctionId,
            @JsonProperty("bidderId") Long bidderId,
            @JsonProperty("amount") Double amount) {
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.amount = amount;
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

    public static class Builder {
        @NotNull(message = "Auction ID is required")
        private Long auctionId;
        @NotNull(message = "Bidder ID is required")
        private Long bidderId;
        @NotNull(message = "Bid amount is required")
        @Positive(message = "Bid amount must be greater than 0")
        private Double amount;

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

        public BidDto build() {
            return new BidDto(auctionId, bidderId, amount);
        }
    }
}