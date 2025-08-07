package com.webapp.realtimeauctionbackend.exceptions;

public class AuctionClosedException extends RuntimeException {
    public AuctionClosedException(String message) {
        super(message);
    }
}
