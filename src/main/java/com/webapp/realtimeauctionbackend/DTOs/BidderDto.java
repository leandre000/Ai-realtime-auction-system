package com.webapp.realtimeauctionbackend.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for bidder data.
 * This class is immutable to ensure thread safety and data consistency.
 */
public final class BidderDto {
    private final Long id;
    private final String username;

    private BidderDto(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public static class Builder {
        @NotNull(message = "ID is required")
        private Long id;
        @NotBlank(message = "Username is required")
        private String username;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public BidderDto build() {
            return new BidderDto(this);
        }
    }
}