package com.webapp.realtimeauctionbackend.DTOs;

import java.time.LocalDateTime;
import java.util.Map;

public final class AdminDashboardStatsDto {
    // User Statistics
    private final int totalUsers;
    private final int activeUsersLast30Days;
    private final double userGrowthRate; // Percentage growth compared to previous period
    private final Map<String, Integer> userRegistrationTrend; // Daily registrations for last 30 days

    // Auction Statistics
    private final int totalAuctions;
    private final int activeAuctions;
    private final int closedAuctions;
    private final double auctionGrowthRate;
    private final Map<String, Integer> auctionCreationTrend;

    // Financial Statistics
    private final double totalRevenue;
    private final double revenueLast30Days;
    private final double revenueGrowthRate;
    private final double averageAuctionValue;
    private final double highestBidAmount;
    private final Map<String, Double> dailyRevenueTrend;

    // Bidding Statistics
    private final int totalBids;
    private final int bidsLast24Hours;
    private final double averageBidAmount;
    private final double bidGrowthRate;
    private final Map<String, Integer> biddingActivityTrend;

    // Performance Metrics
    private final double auctionSuccessRate; // Percentage of auctions that received bids
    private final double averageBidsPerAuction;
    private final double averageAuctionDuration;
    private final double userParticipationRate; // Percentage of users who have placed bids

    // System Health
    private final int activeWebSocketConnections;
    private final double averageResponseTime;
    private final int errorRate; // Errors per 1000 requests

    private AdminDashboardStatsDto(Builder builder) {
        this.totalUsers = builder.totalUsers;
        this.activeUsersLast30Days = builder.activeUsersLast30Days;
        this.userGrowthRate = builder.userGrowthRate;
        this.userRegistrationTrend = builder.userRegistrationTrend;
        this.totalAuctions = builder.totalAuctions;
        this.activeAuctions = builder.activeAuctions;
        this.closedAuctions = builder.closedAuctions;
        this.auctionGrowthRate = builder.auctionGrowthRate;
        this.auctionCreationTrend = builder.auctionCreationTrend;
        this.totalRevenue = builder.totalRevenue;
        this.revenueLast30Days = builder.revenueLast30Days;
        this.revenueGrowthRate = builder.revenueGrowthRate;
        this.averageAuctionValue = builder.averageAuctionValue;
        this.highestBidAmount = builder.highestBidAmount;
        this.dailyRevenueTrend = builder.dailyRevenueTrend;
        this.totalBids = builder.totalBids;
        this.bidsLast24Hours = builder.bidsLast24Hours;
        this.averageBidAmount = builder.averageBidAmount;
        this.bidGrowthRate = builder.bidGrowthRate;
        this.biddingActivityTrend = builder.biddingActivityTrend;
        this.auctionSuccessRate = builder.auctionSuccessRate;
        this.averageBidsPerAuction = builder.averageBidsPerAuction;
        this.averageAuctionDuration = builder.averageAuctionDuration;
        this.userParticipationRate = builder.userParticipationRate;
        this.activeWebSocketConnections = builder.activeWebSocketConnections;
        this.averageResponseTime = builder.averageResponseTime;
        this.errorRate = builder.errorRate;
    }

    // Getters for all fields
    public int getTotalUsers() { return totalUsers; }
    public int getActiveUsersLast30Days() { return activeUsersLast30Days; }
    public double getUserGrowthRate() { return userGrowthRate; }
    public Map<String, Integer> getUserRegistrationTrend() { return userRegistrationTrend; }
    public int getTotalAuctions() { return totalAuctions; }
    public int getActiveAuctions() { return activeAuctions; }
    public int getClosedAuctions() { return closedAuctions; }
    public double getAuctionGrowthRate() { return auctionGrowthRate; }
    public Map<String, Integer> getAuctionCreationTrend() { return auctionCreationTrend; }
    public double getTotalRevenue() { return totalRevenue; }
    public double getRevenueLast30Days() { return revenueLast30Days; }
    public double getRevenueGrowthRate() { return revenueGrowthRate; }
    public double getAverageAuctionValue() { return averageAuctionValue; }
    public double getHighestBidAmount() { return highestBidAmount; }
    public Map<String, Double> getDailyRevenueTrend() { return dailyRevenueTrend; }
    public int getTotalBids() { return totalBids; }
    public int getBidsLast24Hours() { return bidsLast24Hours; }
    public double getAverageBidAmount() { return averageBidAmount; }
    public double getBidGrowthRate() { return bidGrowthRate; }
    public Map<String, Integer> getBiddingActivityTrend() { return biddingActivityTrend; }
    public double getAuctionSuccessRate() { return auctionSuccessRate; }
    public double getAverageBidsPerAuction() { return averageBidsPerAuction; }
    public double getAverageAuctionDuration() { return averageAuctionDuration; }
    public double getUserParticipationRate() { return userParticipationRate; }
    public int getActiveWebSocketConnections() { return activeWebSocketConnections; }
    public double getAverageResponseTime() { return averageResponseTime; }
    public int getErrorRate() { return errorRate; }

    public static class Builder {
        private int totalUsers;
        private int activeUsersLast30Days;
        private double userGrowthRate;
        private Map<String, Integer> userRegistrationTrend;
        private int totalAuctions;
        private int activeAuctions;
        private int closedAuctions;
        private double auctionGrowthRate;
        private Map<String, Integer> auctionCreationTrend;
        private double totalRevenue;
        private double revenueLast30Days;
        private double revenueGrowthRate;
        private double averageAuctionValue;
        private double highestBidAmount;
        private Map<String, Double> dailyRevenueTrend;
        private int totalBids;
        private int bidsLast24Hours;
        private double averageBidAmount;
        private double bidGrowthRate;
        private Map<String, Integer> biddingActivityTrend;
        private double auctionSuccessRate;
        private double averageBidsPerAuction;
        private double averageAuctionDuration;
        private double userParticipationRate;
        private int activeWebSocketConnections;
        private double averageResponseTime;
        private int errorRate;

        public Builder totalUsers(int totalUsers) {
            this.totalUsers = totalUsers;
            return this;
        }

        public Builder activeUsersLast30Days(int activeUsersLast30Days) {
            this.activeUsersLast30Days = activeUsersLast30Days;
            return this;
        }

        public Builder userGrowthRate(double userGrowthRate) {
            this.userGrowthRate = userGrowthRate;
            return this;
        }

        public Builder userRegistrationTrend(Map<String, Integer> userRegistrationTrend) {
            this.userRegistrationTrend = userRegistrationTrend;
            return this;
        }

        public Builder totalAuctions(int totalAuctions) {
            this.totalAuctions = totalAuctions;
            return this;
        }

        public Builder activeAuctions(int activeAuctions) {
            this.activeAuctions = activeAuctions;
            return this;
        }

        public Builder closedAuctions(int closedAuctions) {
            this.closedAuctions = closedAuctions;
            return this;
        }

        public Builder auctionGrowthRate(double auctionGrowthRate) {
            this.auctionGrowthRate = auctionGrowthRate;
            return this;
        }

        public Builder auctionCreationTrend(Map<String, Integer> auctionCreationTrend) {
            this.auctionCreationTrend = auctionCreationTrend;
            return this;
        }

        public Builder totalRevenue(double totalRevenue) {
            this.totalRevenue = totalRevenue;
            return this;
        }

        public Builder revenueLast30Days(double revenueLast30Days) {
            this.revenueLast30Days = revenueLast30Days;
            return this;
        }

        public Builder revenueGrowthRate(double revenueGrowthRate) {
            this.revenueGrowthRate = revenueGrowthRate;
            return this;
        }

        public Builder averageAuctionValue(double averageAuctionValue) {
            this.averageAuctionValue = averageAuctionValue;
            return this;
        }

        public Builder highestBidAmount(double highestBidAmount) {
            this.highestBidAmount = highestBidAmount;
            return this;
        }

        public Builder dailyRevenueTrend(Map<String, Double> dailyRevenueTrend) {
            this.dailyRevenueTrend = dailyRevenueTrend;
            return this;
        }

        public Builder totalBids(int totalBids) {
            this.totalBids = totalBids;
            return this;
        }

        public Builder bidsLast24Hours(int bidsLast24Hours) {
            this.bidsLast24Hours = bidsLast24Hours;
            return this;
        }

        public Builder averageBidAmount(double averageBidAmount) {
            this.averageBidAmount = averageBidAmount;
            return this;
        }

        public Builder bidGrowthRate(double bidGrowthRate) {
            this.bidGrowthRate = bidGrowthRate;
            return this;
        }

        public Builder biddingActivityTrend(Map<String, Integer> biddingActivityTrend) {
            this.biddingActivityTrend = biddingActivityTrend;
            return this;
        }

        public Builder auctionSuccessRate(double auctionSuccessRate) {
            this.auctionSuccessRate = auctionSuccessRate;
            return this;
        }

        public Builder averageBidsPerAuction(double averageBidsPerAuction) {
            this.averageBidsPerAuction = averageBidsPerAuction;
            return this;
        }

        public Builder averageAuctionDuration(double averageAuctionDuration) {
            this.averageAuctionDuration = averageAuctionDuration;
            return this;
        }

        public Builder userParticipationRate(double userParticipationRate) {
            this.userParticipationRate = userParticipationRate;
            return this;
        }

        public Builder activeWebSocketConnections(int activeWebSocketConnections) {
            this.activeWebSocketConnections = activeWebSocketConnections;
            return this;
        }

        public Builder averageResponseTime(double averageResponseTime) {
            this.averageResponseTime = averageResponseTime;
            return this;
        }

        public Builder errorRate(int errorRate) {
            this.errorRate = errorRate;
            return this;
        }

        public AdminDashboardStatsDto build() {
            return new AdminDashboardStatsDto(this);
        }
    }
} 