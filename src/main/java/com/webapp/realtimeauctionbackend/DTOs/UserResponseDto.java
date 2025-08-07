package com.webapp.realtimeauctionbackend.DTOs;

import com.webapp.realtimeauctionbackend.constants.Role;
import com.webapp.realtimeauctionbackend.models.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class UserResponseDto {
    private final Long id;
    private final String name;
    private final String email;
    private final Role role;
    private final Address address;

    private UserResponseDto(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.role = builder.role;
        this.address = builder.address;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public Address getAddress() {
        return address;
    }

    public static class Builder {
        @NotNull(message = "ID is required")
        private Long id;
        @NotBlank(message = "Name is required")
        private String name;
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;
        @NotNull(message = "Role is required")
        private Role role;
        private Address address;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder address(Address address) {
            this.address = address;
            return this;
        }

        public UserResponseDto build() {
            return new UserResponseDto(this);
        }
    }
} 