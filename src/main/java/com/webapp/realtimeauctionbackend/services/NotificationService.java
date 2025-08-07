package com.webapp.realtimeauctionbackend.services;

import com.webapp.realtimeauctionbackend.models.*;
import com.webapp.realtimeauctionbackend.repositories.TransactionNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private TransactionNotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void sendTransactionNotification(Transaction transaction, String message) {
        Person user = transaction.getWallet().getUser();
        
        // Create and save notification
        TransactionNotification notification = new TransactionNotification();
        notification.setTransaction(transaction);
        notification.setUser(user);
        notification.setMessage(message);
        notificationRepository.save(notification);

        // Send WebSocket notification
        String destination = "/user/" + user.getId() + "/queue/notifications";
        messagingTemplate.convertAndSend(destination, notification);
    }

    @Transactional(readOnly = true)
    public List<TransactionNotification> getUnreadNotifications(Long userId) {
        Person user = new Person();
        user.setId(userId);
        return notificationRepository.findByUserAndReadFalseOrderByCreatedAtDesc(user);
    }

    @Transactional(readOnly = true)
    public long getUnreadNotificationCount(Long userId) {
        Person user = new Person();
        user.setId(userId);
        return notificationRepository.countUnreadNotifications(user);
    }

    @Transactional
    public void markNotificationsAsRead(Long userId) {
        Person user = new Person();
        user.setId(userId);
        notificationRepository.markAllAsRead(user);
    }

    @Transactional(readOnly = true)
    public List<TransactionNotification> getRecentNotifications(Long userId, LocalDateTime since) {
        Person user = new Person();
        user.setId(userId);
        return notificationRepository.findRecentNotifications(user, since);
    }
} 