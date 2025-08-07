package com.webapp.realtimeauctionbackend.controllers;

import com.webapp.realtimeauctionbackend.constants.VerificationLevel;
import com.webapp.realtimeauctionbackend.constants.WalletStatus;
import com.webapp.realtimeauctionbackend.models.*;
import com.webapp.realtimeauctionbackend.services.TransactionService;
import com.webapp.realtimeauctionbackend.services.WalletService;
import com.webapp.realtimeauctionbackend.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/balance")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Double> getBalance() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(walletService.getBalance(userId));
    }

    @PostMapping("/deposit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Transaction> deposit(@RequestParam double amount) {
        Long userId = SecurityUtils.getCurrentUserId();
        Transaction transaction = transactionService.createTransaction(
            userId,
            amount,
            TransactionType.DEPOSIT,
            "Wallet deposit",
            null
        );
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/withdraw")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Transaction> withdraw(@RequestParam double amount) {
        Long userId = SecurityUtils.getCurrentUserId();
        Transaction transaction = transactionService.createTransaction(
            userId,
            amount,
            TransactionType.WITHDRAWAL,
            "Wallet withdrawal",
            null
        );
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/transactions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<Transaction>> getTransactions(Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(transactionService.getWalletTransactions(userId, pageable));
    }

    @GetMapping("/transactions/type/{type}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<Transaction>> getTransactionsByType(
            @PathVariable TransactionType type,
            Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(transactionService.getWalletTransactionsByType(userId, type, pageable));
    }

    @GetMapping("/transactions/date-range")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<Transaction>> getTransactionsByDateRange(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(transactionService.getWalletTransactionsByDateRange(userId, start, end, pageable));
    }

    @PostMapping("/verify")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> verifyWallet(@RequestParam VerificationLevel level) {
        Long userId = SecurityUtils.getCurrentUserId();
        walletService.updateVerificationLevel(userId, level);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verification-level")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VerificationLevel> getVerificationLevel() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(walletService.getVerificationLevel(userId));
    }

    @PostMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateWalletStatus(
            @RequestParam Long userId,
            @RequestParam WalletStatus status) {
        walletService.updateStatus(userId, status);
        return ResponseEntity.ok().build();
    }
} 