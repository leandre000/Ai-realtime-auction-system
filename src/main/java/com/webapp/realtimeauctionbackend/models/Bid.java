package com.webapp.realtimeauctionbackend.models;

import com.webapp.realtimeauctionbackend.DTOs.BidderDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Bid amount is required")
    @Min(value = 0, message = "Bid amount must be greater than or equal to 0")
    private double amount;

    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "auction_id")
    @NotNull(message = "Auction is required")
    private Auction auction;

    @ManyToOne
    @JoinColumn(name = "bidder_id")
    @NotNull(message = "Bidder is required")
    private Person bidder;

    public Bid() {
        this.timestamp = LocalDateTime.now();
    }

    public Bid(double amount, Auction auction, Person bidder) {
        this.amount = amount;
        this.auction = auction;
        this.bidder = bidder;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Bid amount cannot be negative");
        }
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public Person getBidder() {
        return bidder;
    }

    public void setBidder(Person bidder) {
        this.bidder = bidder;
    }
}