package com.webapp.realtimeauctionbackend.repositories;

import com.webapp.realtimeauctionbackend.models.Person;
import com.webapp.realtimeauctionbackend.models.Transaction;
import com.webapp.realtimeauctionbackend.models.TransactionNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionNotificationRepository extends JpaRepository<TransactionNotification, Long> {
    Page<TransactionNotification> findByUserOrderByCreatedAtDesc(Person user, Pageable pageable);
    
    List<TransactionNotification> findByUserAndReadFalseOrderByCreatedAtDesc(Person user);
    
    @Modifying
    @Query("UPDATE TransactionNotification n SET n.read = true WHERE n.user = :user AND n.read = false")
    void markAllAsRead(@Param("user") Person user);
    
    @Query("SELECT COUNT(n) FROM TransactionNotification n WHERE n.user = :user AND n.read = false")
    long countUnreadNotifications(@Param("user") Person user);
    
    @Query("SELECT n FROM TransactionNotification n WHERE n.user = :user AND n.createdAt >= :since ORDER BY n.createdAt DESC")
    List<TransactionNotification> findRecentNotifications(@Param("user") Person user, @Param("since") LocalDateTime since);

    @Query("SELECT n FROM TransactionNotification n WHERE n.transaction = :transaction ORDER BY n.createdAt DESC")
    List<TransactionNotification> findByTransaction(@Param("transaction") Transaction transaction);

    @Query("SELECT n FROM TransactionNotification n WHERE n.user = :user AND n.createdAt BETWEEN :start AND :end ORDER BY n.createdAt DESC")
    List<TransactionNotification> findByUserAndDateRange(
            @Param("user") Person user,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(n) FROM TransactionNotification n WHERE n.user = :user AND n.createdAt BETWEEN :start AND :end")
    long countByUserAndDateRange(
            @Param("user") Person user,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
} 