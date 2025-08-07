package com.webapp.realtimeauctionbackend.models;

public enum TransactionType {
    DEPOSIT,        // Adding funds to wallet
    WITHDRAWAL,     // Removing funds from wallet
    BID,            // Placing a bid
    WINNING_BID,    // Winning an auction
    REFUND,         // Refund for cancelled auction
    FEE,            // Service fees
    TRANSFER        // Transfer between wallets
} 