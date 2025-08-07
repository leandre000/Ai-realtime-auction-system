package com.webapp.realtimeauctionbackend.services;

import com.webapp.realtimeauctionbackend.constants.VerificationLevel;
import com.webapp.realtimeauctionbackend.exceptions.TransactionException;
import com.webapp.realtimeauctionbackend.exceptions.WalletException;
import com.webapp.realtimeauctionbackend.models.*;
import com.webapp.realtimeauctionbackend.repositories.TransactionRepository;
import com.webapp.realtimeauctionbackend.constants.TransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private NotificationService notificationService;

    private static final double TRANSACTION_FEE_PERCENTAGE = 0.01; // 1% fee
    private static final double VERIFIED_USER_FEE_REDUCTION = 0.5; // 50% reduction for verified users

    @Transactional
    @CacheEvict(value = "transactions", key = "#userId")
    public Transaction createTransaction(Long userId, double amount, TransactionType type, String description, String referenceId) {
        Wallet wallet = walletService.getWalletByUserId(userId);
        
        // Calculate transaction fee
        double fee = calculateTransactionFee(userId, amount, type);
        double totalAmount = amount + fee;
        
        // Validate transaction based on type
        switch (type) {
            case WITHDRAWAL, BID -> {
                if (!walletService.hasSufficientBalance(userId, totalAmount)) {
                    throw new TransactionException("Insufficient balance for transaction and fee");
                }
                if (!walletService.isWalletActive(userId)) {
                    throw new TransactionException("Wallet is not active");
                }
            }
            case DEPOSIT -> {
                if (amount <= 0) {
                    throw new TransactionException("Deposit amount must be positive");
                }
            }
        }

        // Create and save transaction
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setDescription(description);
        transaction.setReferenceId(referenceId);

        try {
            // Update wallet balance based on transaction type
            switch (type) {
                case DEPOSIT -> walletService.updateBalance(userId, amount - fee);
                case WITHDRAWAL, BID -> walletService.updateBalance(userId, -(amount + fee));
            }
            
            transaction.setStatus(TransactionStatus.COMPLETED);
            Transaction savedTransaction = transactionRepository.save(transaction);

            // Send notification asynchronously
            CompletableFuture.runAsync(() -> 
                notificationService.sendTransactionNotification(
                    savedTransaction,
                    String.format("Transaction %s completed. Amount: %.2f, Fee: %.2f", 
                        type, amount, fee)
                )
            );

            return savedTransaction;
        } catch (WalletException e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new TransactionException("Transaction failed: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "transactions", key = "#userId")
    public Page<Transaction> getWalletTransactions(Long userId, Pageable pageable) {
        Wallet wallet = walletService.getWalletByUserId(userId);
        return transactionRepository.findByWallet(wallet, pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "transactions", key = "#userId + '_' + #type")
    public Page<Transaction> getWalletTransactionsByType(Long userId, TransactionType type, Pageable pageable) {
        Wallet wallet = walletService.getWalletByUserId(userId);
        return transactionRepository.findByWalletAndType(wallet, type, pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "transactions", key = "#userId + '_' + #status")
    public Page<Transaction> getWalletTransactionsByStatus(Long userId, TransactionStatus status, Pageable pageable) {
        Wallet wallet = walletService.getWalletByUserId(userId);
        return transactionRepository.findByWalletAndStatus(wallet, status, pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "transactions", key = "#userId + '_' + #start + '_' + #end")
    public Page<Transaction> getWalletTransactionsByDateRange(
            Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        Wallet wallet = walletService.getWalletByUserId(userId);
        return transactionRepository.findByWalletAndTimestampBetween(wallet, start, end, pageable);
    }

    @Transactional
    @CacheEvict(value = "transactions", key = "#transactionId")
    public void reverseTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionException("Transaction not found"));

        if (transaction.getStatus() != TransactionStatus.COMPLETED) {
            throw new TransactionException("Only completed transactions can be reversed");
        }

        try {
            // Reverse the transaction amount
            switch (transaction.getType()) {
                case DEPOSIT -> walletService.updateBalance(
                    transaction.getWallet().getUser().getId(), 
                    -transaction.getAmount()
                );
                case WITHDRAWAL, BID -> walletService.updateBalance(
                    transaction.getWallet().getUser().getId(), 
                    transaction.getAmount()
                );
            }

            transaction.setStatus(TransactionStatus.REVERSED);
            transactionRepository.save(transaction);

            // Send notification asynchronously
            CompletableFuture.runAsync(() -> 
                notificationService.sendTransactionNotification(
                    transaction,
                    String.format("Transaction %d has been reversed", transactionId)
                )
            );
        } catch (WalletException e) {
            throw new TransactionException("Failed to reverse transaction: " + e.getMessage());
        }
    }

    private double calculateTransactionFee(Long userId, double amount, TransactionType type) {
        if (type == TransactionType.DEPOSIT) {
            return 0; // No fee for deposits
        }

        double fee = amount * TRANSACTION_FEE_PERCENTAGE;
        
        // Reduce fee for verified users
        if (walletService.getVerificationLevel(userId) == VerificationLevel.VERIFIED) {
            fee *= VERIFIED_USER_FEE_REDUCTION;
        }

        return fee;
    }
} 