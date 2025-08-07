package com.webapp.realtimeauctionbackend.services;

import com.webapp.realtimeauctionbackend.DTOs.AdminDashboardStatsDto;
import com.webapp.realtimeauctionbackend.constants.AuctionStatus;
import com.webapp.realtimeauctionbackend.models.Auction;
import com.webapp.realtimeauctionbackend.models.Bid;
import com.webapp.realtimeauctionbackend.models.Person;
import com.webapp.realtimeauctionbackend.repositories.AuctionRepository;
import com.webapp.realtimeauctionbackend.repositories.BidRepository;
import com.webapp.realtimeauctionbackend.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
public class AdminStatisticsService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    // Cache for dashboard statistics
    private AdminDashboardStatsDto cachedStats;
    private LocalDateTime lastUpdateTime;

    @Scheduled(fixedRate = 300000) // Update every 5 minutes
    @Transactional(readOnly = true)
    public void updateStatistics() {
        cachedStats = calculateStatistics();
        lastUpdateTime = LocalDateTime.now();
    }

    public AdminDashboardStatsDto getDashboardStatistics() {
        if (cachedStats == null || lastUpdateTime == null || 
            ChronoUnit.MINUTES.between(lastUpdateTime, LocalDateTime.now()) >= 5) {
            updateStatistics();
        }
        return cachedStats;
    }

    private AdminDashboardStatsDto calculateStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysAgo = now.minusDays(30);
        LocalDateTime twentyFourHoursAgo = now.minusHours(24);

        // User Statistics
        int totalUsers = (int) personRepository.count();
        int activeUsersLast30Days = (int) personRepository.countByLastLoginAfter(thirtyDaysAgo);
        double userGrowthRate = calculateGrowthRate(
            personRepository.countByCreatedAtBefore(thirtyDaysAgo),
            totalUsers
        );
        Map<String, Integer> userRegistrationTrend = getDailyTrend(
            personRepository::countByCreatedAtBetween,
            thirtyDaysAgo,
            now
        );

        // Auction Statistics
        int totalAuctions = (int) auctionRepository.count();
        int activeAuctions = (int) auctionRepository.countByStatus(AuctionStatus.ACTIVE);
        int closedAuctions = (int) auctionRepository.countByStatus(AuctionStatus.CLOSED);
        double auctionGrowthRate = calculateGrowthRate(
            auctionRepository.countByCreatedAtBefore(thirtyDaysAgo),
            totalAuctions
        );
        Map<String, Integer> auctionCreationTrend = getDailyTrend(
            auctionRepository::countByCreatedAtBetween,
            thirtyDaysAgo,
            now
        );

        // Financial Statistics
        List<Auction> closedAuctionsList = auctionRepository.findByStatus(AuctionStatus.CLOSED);
        double totalRevenue = closedAuctionsList.stream()
            .mapToDouble(Auction::getCurrentPrice)
            .sum();
        double revenueLast30Days = closedAuctionsList.stream()
            .filter(auction -> auction.getEndTime().isAfter(thirtyDaysAgo))
            .mapToDouble(Auction::getCurrentPrice)
            .sum();
        double revenueGrowthRate = calculateGrowthRate(
            closedAuctionsList.stream()
                .filter(auction -> auction.getEndTime().isBefore(thirtyDaysAgo))
                .mapToDouble(Auction::getCurrentPrice)
                .sum(),
            totalRevenue
        );
        double averageAuctionValue = totalAuctions > 0 ? totalRevenue / totalAuctions : 0;
        double highestBidAmount = closedAuctionsList.stream()
            .mapToDouble(Auction::getCurrentPrice)
            .max()
            .orElse(0);
        Map<String, Double> dailyRevenueTrend = getDailyRevenueTrend(closedAuctionsList, thirtyDaysAgo, now);

        int totalBids = (int) bidRepository.count();
        int bidsLast24Hours = (int) bidRepository.countByCreatedAtAfter(twentyFourHoursAgo);
        double averageBidAmount = totalBids > 0 ? 
            bidRepository.findAll().stream()
                .mapToDouble(Bid::getAmount)
                .average()
                .orElse(0) : 0;
        double bidGrowthRate = calculateGrowthRate(
            bidRepository.countByCreatedAtBefore(thirtyDaysAgo),
            totalBids
        );
        Map<String, Integer> biddingActivityTrend = getDailyTrend(
            bidRepository::countByCreatedAtBetween,
            thirtyDaysAgo,
            now
        );

        // Performance Metrics
        double auctionSuccessRate = totalAuctions > 0 ? 
            (double) closedAuctionsList.stream()
                .filter(auction -> auction.getBids().size() > 0)
                .count() / totalAuctions * 100 : 0;
        double averageBidsPerAuction = totalAuctions > 0 ? 
            (double) totalBids / totalAuctions : 0;
        double averageAuctionDuration = totalAuctions > 0 ?
            closedAuctionsList.stream()
                .mapToLong(auction -> ChronoUnit.HOURS.between(
                    auction.getStartTime(),
                    auction.getEndTime()
                ))
                .average()
                .orElse(0) : 0;
        double userParticipationRate = totalUsers > 0 ?
            (double) personRepository.countByBidsIsNotEmpty() / totalUsers * 100 : 0;

        // System Health (These would be implemented based on your monitoring system)
        int activeWebSocketConnections = 0; // Implement based on your WebSocket setup
        double averageResponseTime = 0; // Implement based on your monitoring
        int errorRate = 0; // Implement based on your error tracking

        return new AdminDashboardStatsDto.Builder()
            .totalUsers(totalUsers)
            .activeUsersLast30Days(activeUsersLast30Days)
            .userGrowthRate(userGrowthRate)
            .userRegistrationTrend(userRegistrationTrend)
            .totalAuctions(totalAuctions)
            .activeAuctions(activeAuctions)
            .closedAuctions(closedAuctions)
            .auctionGrowthRate(auctionGrowthRate)
            .auctionCreationTrend(auctionCreationTrend)
            .totalRevenue(totalRevenue)
            .revenueLast30Days(revenueLast30Days)
            .revenueGrowthRate(revenueGrowthRate)
            .averageAuctionValue(averageAuctionValue)
            .highestBidAmount(highestBidAmount)
            .dailyRevenueTrend(dailyRevenueTrend)
            .totalBids(totalBids)
            .bidsLast24Hours(bidsLast24Hours)
            .averageBidAmount(averageBidAmount)
            .bidGrowthRate(bidGrowthRate)
            .biddingActivityTrend(biddingActivityTrend)
            .auctionSuccessRate(auctionSuccessRate)
            .averageBidsPerAuction(averageBidsPerAuction)
            .averageAuctionDuration(averageAuctionDuration)
            .userParticipationRate(userParticipationRate)
            .activeWebSocketConnections(activeWebSocketConnections)
            .averageResponseTime(averageResponseTime)
            .errorRate(errorRate)
            .build();
    }

    private double calculateGrowthRate(double previousValue, double currentValue) {
        if (previousValue == 0) return 0;
        return ((currentValue - previousValue) / previousValue) * 100;
    }

    private Map<String, Integer> getDailyTrend(
        BiFunction<LocalDateTime, LocalDateTime, Long> countFunction,
        LocalDateTime start,
        LocalDateTime end
    ) {
        Map<String, Integer> trend = new TreeMap<>();
        LocalDateTime current = start;
        while (current.isBefore(end)) {
            LocalDateTime nextDay = current.plusDays(1);
            long count = countFunction.apply(current, nextDay);
            trend.put(current.toLocalDate().toString(), (int) count);
            current = nextDay;
        }
        return trend;
    }

    private Map<String, Double> getDailyRevenueTrend(
        List<Auction> closedAuctions,
        LocalDateTime start,
        LocalDateTime end
    ) {
        Map<String, Double> trend = new TreeMap<>();
        LocalDateTime current = start;
        while (current.isBefore(end)) {
            LocalDateTime nextDay = current.plusDays(1);
            LocalDateTime finalCurrent = current;
            double dailyRevenue = closedAuctions.stream()
                .filter(auction -> auction.getEndTime().isAfter(finalCurrent) &&
                                 auction.getEndTime().isBefore(nextDay))
                .mapToDouble(Auction::getCurrentPrice)
                .sum();
            trend.put(current.toLocalDate().toString(), dailyRevenue);
            current = nextDay;
        }
        return trend;
    }

    @FunctionalInterface
    private interface BiFunction<T, U, R> {
        R apply(T t, U u);
    }
} 