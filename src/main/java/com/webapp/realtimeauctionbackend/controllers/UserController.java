package com.webapp.realtimeauctionbackend.controllers;

import com.webapp.realtimeauctionbackend.DTOs.UserResponseDto;
import com.webapp.realtimeauctionbackend.DTOs.UserUpdateDto;
import com.webapp.realtimeauctionbackend.DTOs.BidResponseDto;
import com.webapp.realtimeauctionbackend.DTOs.AuctionResponseDto;
import com.webapp.realtimeauctionbackend.DTOs.SellerDto;
import com.webapp.realtimeauctionbackend.models.Person;
import com.webapp.realtimeauctionbackend.repositories.PersonRepository;
import com.webapp.realtimeauctionbackend.repositories.BidRepository;
import com.webapp.realtimeauctionbackend.repositories.AuctionRepository;
import com.webapp.realtimeauctionbackend.utils.SecurityUtils;
import com.webapp.realtimeauctionbackend.constants.AuctionStatus;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final PersonRepository personRepository;
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;

    public UserController(PersonRepository personRepository,
                         BidRepository bidRepository,
                         AuctionRepository auctionRepository) {
        this.personRepository = personRepository;
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        Person person = personRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponseDto response = new UserResponseDto.Builder()
                .id(person.getId())
                .name(person.getName())
                .email(person.getEmail())
                .role(person.getRole())
                .address(person.getAddress())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> updateProfile(@Valid @RequestBody UserUpdateDto updateDto) {
        Long userId = SecurityUtils.getCurrentUserId();
        Person person = personRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        person.setName(updateDto.getName());
        person.setEmail(updateDto.getEmail());
        person.setAddress(updateDto.getAddress());

        Person updatedPerson = personRepository.save(person);

        UserResponseDto response = new UserResponseDto.Builder()
                .id(updatedPerson.getId())
                .name(updatedPerson.getName())
                .email(updatedPerson.getEmail())
                .role(updatedPerson.getRole())
                .address(updatedPerson.getAddress())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        Person person = personRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        personRepository.delete(person);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/bids")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<BidResponseDto>> getUserBids(Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();
        Person user = personRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<BidResponseDto> bids = bidRepository.findByBidder(user, pageable)
                .map(bid -> new BidResponseDto.Builder()
                        .id(bid.getId())
                        .auctionId(bid.getAuction().getId())
                        .bidderId(bid.getBidder().getId())
                        .amount(bid.getAmount())
                        .timestamp(bid.getTimestamp())
                        .build());

        return ResponseEntity.ok(bids);
    }

    @GetMapping("/auctions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<AuctionResponseDto>> getUserAuctions(Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();
        Person user = personRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<AuctionResponseDto> auctions = auctionRepository.findBySeller(user, pageable)
                .map(auction -> new AuctionResponseDto.Builder()
                        .id(auction.getId())
                        .title(auction.getTitle())
                        .description(auction.getDescription())
                        .startingPrice(auction.getStartingPrice())
                        .currentPrice(auction.getCurrentPrice())
                        .status(auction.getStatus())
                        .startTime(auction.getStartTime())
                        .endTime(auction.getEndTime())
                        .seller(new SellerDto.Builder()
                                .id(user.getId())
                                .name(user.getName())
                                .email(user.getEmail())
                                .build())
                        .bids(auction.getBids().stream()
                                .map(bid -> new BidResponseDto.Builder()
                                        .id(bid.getId())
                                        .auctionId(bid.getAuction().getId())
                                        .bidderId(bid.getBidder().getId())
                                        .amount(bid.getAmount())
                                        .timestamp(bid.getTimestamp())
                                        .build())
                                .collect(Collectors.toList()))
                        .build());

        return ResponseEntity.ok(auctions);
    }

    @GetMapping("/auctions/won")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<AuctionResponseDto>> getWonAuctions(Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();
        Person user = personRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<AuctionResponseDto> auctions = auctionRepository.findByCurrentBidderAndStatus(user, AuctionStatus.CLOSED, pageable)
                .map(auction -> new AuctionResponseDto.Builder()
                        .id(auction.getId())
                        .title(auction.getTitle())
                        .description(auction.getDescription())
                        .startingPrice(auction.getStartingPrice())
                        .currentPrice(auction.getCurrentPrice())
                        .status(auction.getStatus())
                        .startTime(auction.getStartTime())
                        .endTime(auction.getEndTime())
                        .seller(new SellerDto.Builder()
                                .id(auction.getSeller().getId())
                                .name(auction.getSeller().getName())
                                .email(auction.getSeller().getEmail())
                                .build())
                        .bids(auction.getBids().stream()
                                .map(bid -> new BidResponseDto.Builder()
                                        .id(bid.getId())
                                        .auctionId(bid.getAuction().getId())
                                        .bidderId(bid.getBidder().getId())
                                        .amount(bid.getAmount())
                                        .timestamp(bid.getTimestamp())
                                        .build())
                                .collect(Collectors.toList()))
                        .build());

        return ResponseEntity.ok(auctions);
    }
} 