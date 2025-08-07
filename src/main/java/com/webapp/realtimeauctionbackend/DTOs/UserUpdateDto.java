package com.webapp.realtimeauctionbackend.DTOs;

import com.webapp.realtimeauctionbackend.models.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class UserUpdateDto {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private final String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private final String email;
    
    private final Address address;

    private UserUpdateDto(Builder builder) {
        this.name = builder.name;
        this.email = builder.email;
        this.address = builder.address;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public static class Builder {
        private String name;
        private String email;
        private Address address;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder address(Address address) {
            this.address = address;
            return this;
        }

        public UserUpdateDto build() {
            return new UserUpdateDto(this);
        }
    }
} 