package com.webapp.realtimeauctionbackend.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
    @JsonProperty("token") String token
) {
}
