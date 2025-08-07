package com.webapp.realtimeauctionbackend.exceptions;

public class InvalidAuctionException extends RuntimeException {
    public InvalidAuctionException(String message) {
        super(message);
    }
}
