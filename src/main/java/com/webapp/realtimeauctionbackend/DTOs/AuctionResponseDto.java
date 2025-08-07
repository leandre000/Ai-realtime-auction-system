package com.webapp.realtimeauctionbackend.DTOs;

import com.webapp.realtimeauctionbackend.constants.AuctionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for auction response data.
 * This class is immutable to ensure thread safety and data consistency.
 */
public final class AuctionResponseDto {
    private final Long id;
    private final String title;
    private final String description;
    private final double startingPrice;
    private final double currentPrice;
    private final AuctionStatus status;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final SellerDto seller;
    private final List<BidResponseDto> bids;

    public AuctionResponseDto(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.startingPrice = builder.startingPrice;
        this.currentPrice = builder.currentPrice;
        this.status = builder.status;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.seller = builder.seller;
        this.bids = builder.bids;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public SellerDto getSeller() {
        return seller;
    }

    public List<BidResponseDto> getBids() {
        return List.copyOf(bids); // Return immutable copy
    }

    public static class Builder {
        private Long id;
        @NotBlank(message = "Title is required")
        private String title;
        @NotBlank(message = "Description is required")
        private String description;
        @Positive(message = "Starting price must be positive")
        private double startingPrice;
        @Positive(message = "Current price must be positive")
        private double currentPrice;
        @NotNull(message = "Status is required")
        private AuctionStatus status;
        @NotNull(message = "Start time is required")
        private LocalDateTime startTime;
        @NotNull(message = "End time is required")
        private LocalDateTime endTime;
        @NotNull(message = "Seller is required")
        private SellerDto seller;
        private List<BidResponseDto> bids;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder startingPrice(double startingPrice) {
            this.startingPrice = startingPrice;
            return this;
        }

        public Builder currentPrice(double currentPrice) {
            this.currentPrice = currentPrice;
            return this;
        }

        public Builder status(AuctionStatus status) {
            this.status = status;
            return this;
        }

        public Builder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder seller(SellerDto seller) {
            this.seller = seller;
            return this;
        }

        public Builder bids(List<BidResponseDto> bids) {
            this.bids = bids;
            return this;
        }

        public AuctionResponseDto build() {
            return new AuctionResponseDto(this);
        }
    }
}
