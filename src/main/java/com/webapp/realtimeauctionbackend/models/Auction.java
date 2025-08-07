package com.webapp.realtimeauctionbackend.models;

import com.webapp.realtimeauctionbackend.constants.AuctionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    @NotNull(message = "Starting price is required")
    @Min(value = 0, message = "Starting price must be greater than or equal to 0")
    private double startingPrice;

    @NotNull(message = "Current price is required")
    @Min(value = 0, message = "Current price must be greater than or equal to 0")
    private double currentPrice;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    private AuctionStatus status = AuctionStatus.SCHEDULED;

    @ManyToOne
    @JoinColumn(name = "seller_id", referencedColumnName = "id")
    @NotNull(message = "Seller is required")
    private Person seller;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL)
    private List<Bid> bids;

    @ManyToOne
    @JoinColumn(name = "current_bidder_id")
    private Person currentBidder;

    @ManyToMany
    @JoinTable(
        name = "auction_categories",
        joinColumns = @JoinColumn(name = "auction_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "auction_tags",
        joinColumns = @JoinColumn(name = "auction_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    public Auction() {}

    public Auction(String title, String description, double startingPrice, LocalDateTime startTime, LocalDateTime endTime) {
        this.title = title;
        this.description = description;
        this.startingPrice = startingPrice;
        this.currentPrice = startingPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = AuctionStatus.SCHEDULED;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public double getStartingPrice() {
        return startingPrice;
    }
    public void setStartingPrice(double startingPrice) {
        this.startingPrice = startingPrice;
    }
    public double getCurrentPrice() {
        return currentPrice;
    }
    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    public AuctionStatus getStatus() {
        return status;
    }
    public void setStatus(AuctionStatus status) {
        if (this.status == AuctionStatus.CLOSED) {
            throw new IllegalStateException("Cannot change status of a closed auction");
        }
        this.status = status;
    }
    public Person getSeller() {
        return seller;
    }
    public void setSeller(Person seller) {
        this.seller = seller;
    }
    public List<Bid> getBids() {
        return bids;
    }
    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }

    public boolean isActive() {
        return status == AuctionStatus.ACTIVE;
    }

    public boolean isClosed() {
        return status == AuctionStatus.CLOSED;
    }

    public boolean isScheduled() {
        return status == AuctionStatus.SCHEDULED;
    }

    public Person getCurrentBidder() {
        return currentBidder;
    }

    public void setCurrentBidder(Person currentBidder) {
        this.currentBidder = currentBidder;
    }
}