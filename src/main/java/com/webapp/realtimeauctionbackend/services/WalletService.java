package com.webapp.realtimeauctionbackend.services;

import com.webapp.realtimeauctionbackend.constants.VerificationLevel;
import com.webapp.realtimeauctionbackend.constants.WalletStatus;
import com.webapp.realtimeauctionbackend.exceptions.WalletException;
import com.webapp.realtimeauctionbackend.models.Person;
import com.webapp.realtimeauctionbackend.models.Wallet;
import com.webapp.realtimeauctionbackend.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Transactional
    public Wallet createWallet(Person user, double initialBalance) {
        // Check if user already has a wallet
        if (walletRepository.findByUserId(user.getId()).isPresent()) {
            throw new WalletException("User already has a wallet");
        }

        Wallet wallet = new Wallet(user, initialBalance);
        return walletRepository.save(wallet);
    }

    @Transactional(readOnly = true)
    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletException("Wallet not found for user: " + userId));
    }

    @Transactional
    public Wallet updateBalance(Long userId, double amount) {
        Wallet wallet = getWalletByUserId(userId);
        
        // Check wallet status
        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new WalletException("Wallet is not active. Current status: " + wallet.getStatus());
        }

        // Check if balance will be negative
        double newBalance = wallet.getBalance() + amount;
        if (newBalance < 0) {
            throw new WalletException("Insufficient balance");
        }

        wallet.setBalance(newBalance);
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet updateStatus(Long userId, WalletStatus newStatus) {
        Wallet wallet = getWalletByUserId(userId);
        wallet.setStatus(newStatus);
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet updateVerificationLevel(Long userId, VerificationLevel newLevel) {
        Wallet wallet = getWalletByUserId(userId);
        wallet.setVerificationLevel(newLevel);
        return walletRepository.save(wallet);
    }

    @Transactional(readOnly = true)
    public boolean isWalletActive(Long userId) {
        Optional<Wallet> wallet = walletRepository.findByUserIdAndStatus(userId, WalletStatus.ACTIVE);
        return wallet.isPresent();
    }

    @Transactional(readOnly = true)
    public boolean hasSufficientBalance(Long userId, double amount) {
        Wallet wallet = getWalletByUserId(userId);
        return wallet.getBalance() >= amount;
    }

    @Transactional(readOnly = true)
    public VerificationLevel getVerificationLevel(Long userId) {
        Wallet wallet = getWalletByUserId(userId);
        return wallet.getVerificationLevel();
    }

    @Transactional(readOnly = true)
    public double getBalance(Long userId) {
        Wallet wallet = getWalletByUserId(userId);
        return wallet.getBalance();
    }
} 