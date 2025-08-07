package com.webapp.realtimeauctionbackend.services;

import com.webapp.realtimeauctionbackend.DTOs.AuctionCreateDto;
import com.webapp.realtimeauctionbackend.DTOs.AuctionResponseDto;
import com.webapp.realtimeauctionbackend.DTOs.AuctionUpdateDto;
import com.webapp.realtimeauctionbackend.exceptions.InvalidAuctionException;
import com.webapp.realtimeauctionbackend.exceptions.ResourceNotFoundException;
import com.webapp.realtimeauctionbackend.mappers.AuctionMapper;
import com.webapp.realtimeauctionbackend.models.Auction;
import com.webapp.realtimeauctionbackend.constants.AuctionStatus;
import com.webapp.realtimeauctionbackend.models.Person;
import com.webapp.realtimeauctionbackend.repositories.AuctionRepository;
import com.webapp.realtimeauctionbackend.repositories.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuctionService {
    private static final Logger logger = LoggerFactory.getLogger(AuctionService.class);
    private final AuctionRepository auctionRepository;
    private final PersonRepository personRepository;
    private final AuctionMapper auctionMapper;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository,
                         PersonRepository personRepository,
                         AuctionMapper auctionMapper) {
        this.auctionRepository = auctionRepository;
        this.personRepository = personRepository;
        this.auctionMapper = auctionMapper;
    }

    @Transactional
    public AuctionResponseDto createAuction(AuctionCreateDto dto, Long userId) {
        logger.info("Creating auction for user ID: {}", userId);
        logger.debug("Auction details - Title: {}, Start Time: {}, End Time: {}", 
            dto.getTitle(), dto.getStartTime(), dto.getEndTime());

        validateAuctionDates(dto.getStartTime(), dto.getEndTime());

        Person seller = personRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        Auction auction = new Auction();
        auction.setTitle(dto.getTitle());
        auction.setDescription(dto.getDescription());
        auction.setStartingPrice(dto.getStartingPrice());
        auction.setStartTime(dto.getStartTime());
        auction.setEndTime(dto.getEndTime());
        auction.setSeller(seller);
        auction.setCurrentPrice(dto.getStartingPrice());
        auction.setStatus(calculateInitialStatus(dto.getStartTime()));

        Auction savedAuction = auctionRepository.save(auction);
        logger.info("Created auction ID: {} with status: {}", savedAuction.getId(), savedAuction.getStatus());
        logger.debug("Auction details - Title: {}, Starting Price: {}, Current Price: {}", 
            savedAuction.getTitle(), savedAuction.getStartingPrice(), savedAuction.getCurrentPrice());

        return auctionMapper.auctionToResponseDto(savedAuction);
    }

    private void validateAuctionDates(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        
        if (startTime.isBefore(now)) {
            logger.error("Invalid start time: {} is in the past", startTime);
            throw new InvalidAuctionException("Auction start time cannot be in the past");
        }
        
        if (endTime.isBefore(now)) {
            logger.error("Invalid end time: {} is in the past", endTime);
            throw new InvalidAuctionException("Auction end time cannot be in the past");
        }
        
        if (endTime.isBefore(startTime)) {
            logger.error("Invalid auction duration - End time: {} is before start time: {}", endTime, startTime);
            throw new InvalidAuctionException("Auction end time must be after start time");
        }
        
        if (startTime.equals(endTime)) {
            logger.error("Invalid auction duration - Start time: {} equals end time: {}", startTime, endTime);
            throw new InvalidAuctionException("Auction start time cannot equal end time");
        }
    }

    private AuctionStatus calculateInitialStatus(LocalDateTime startTime) {
        LocalDateTime now = LocalDateTime.now();
        if (startTime.isBefore(now)) {
            logger.warn("Auction start time {} is in the past, setting status to ACTIVE", startTime);
            return AuctionStatus.ACTIVE;
        }
        return AuctionStatus.SCHEDULED;
    }

    @Transactional(readOnly = true)
    public AuctionResponseDto getAuctionById(Long auctionId) {
        logger.info("Fetching auction with ID: {}", auctionId);
        return auctionRepository.findById(auctionId)
                .map(auction -> {
                    logger.debug("Found auction - Title: {}, Status: {}, Current Price: {}", 
                        auction.getTitle(), auction.getStatus(), auction.getCurrentPrice());
                    return auctionMapper.auctionToResponseDto(auction);
                })
                .orElseThrow(() -> {
                    logger.error("Auction not found with ID: {}", auctionId);
                    return new ResourceNotFoundException("Auction not found with ID: " + auctionId);
                });
    }

    @Transactional(readOnly = true)
    public Page<AuctionResponseDto> getAllAuctions(String status, Pageable pageable) {
        logger.info("Fetching auctions with status: {}, page: {}, size: {}", 
            status, pageable.getPageNumber(), pageable.getPageSize());
        
        if (status != null) {
            try {
                AuctionStatus auctionStatus = AuctionStatus.valueOf(status.toUpperCase());
                Page<AuctionResponseDto> auctions = auctionRepository.findByStatus(auctionStatus, pageable)
                        .map(auctionMapper::auctionToResponseDto);
                logger.debug("Found {} auctions with status: {}", auctions.getTotalElements(), status);
                return auctions;
            } catch (IllegalArgumentException e) {
                logger.error("Invalid auction status: {}", status);
                throw new InvalidAuctionException("Invalid auction status: " + status);
            }
        }
        
        Page<AuctionResponseDto> auctions = auctionRepository.findAll(pageable)
                .map(auctionMapper::auctionToResponseDto);
        logger.debug("Found {} total auctions", auctions.getTotalElements());
        return auctions;
    }

    @Transactional
    @PreAuthorize("@auctionSecurityService.isOwner(#auctionId, authentication.principal)")
    public AuctionResponseDto updateAuction(Long auctionId, AuctionUpdateDto dto, Long userId) {
        logger.info("Updating auction ID: {} for user ID: {}", auctionId, userId);
        
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> {
                    logger.error("Auction not found with ID: {}", auctionId);
                    return new ResourceNotFoundException("Auction not found with ID: " + auctionId);
                });

        if (auction.isClosed()) {
            logger.error("Cannot update closed auction ID: {}", auctionId);
            throw new InvalidAuctionException("Cannot update a closed auction");
        }

        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            logger.debug("Validating updated auction dates - Start: {}, End: {}", 
                dto.getStartTime(), dto.getEndTime());
            validateAuctionDates(dto.getStartTime(), dto.getEndTime());
            auction.setStartTime(dto.getStartTime());
            auction.setEndTime(dto.getEndTime());
        }

        if (dto.getTitle() != null) {
            logger.debug("Updating auction title from: {} to: {}", auction.getTitle(), dto.getTitle());
            auction.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null) {
            logger.debug("Updating auction description");
            auction.setDescription(dto.getDescription());
        }

        if (dto.getStartingPrice() >= 0 && auction.getBids().isEmpty()) {
            logger.debug("Updating auction starting price from: {} to: {}", 
                auction.getStartingPrice(), dto.getStartingPrice());
            auction.setStartingPrice(dto.getStartingPrice());
            auction.setCurrentPrice(dto.getStartingPrice());
        }

        Auction updatedAuction = auctionRepository.save(auction);
        logger.info("Updated auction ID: {} successfully", updatedAuction.getId());
        logger.debug("Updated auction details - Title: {}, Status: {}, Current Price: {}", 
            updatedAuction.getTitle(), updatedAuction.getStatus(), updatedAuction.getCurrentPrice());

        return auctionMapper.auctionToResponseDto(updatedAuction);
    }

    @Transactional
    @PreAuthorize("@auctionSecurityService.isOwner(#auctionId, authentication.principal)")
    public void deleteAuction(Long auctionId, Long userId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with ID: " + auctionId));

        if (!auction.getBids().isEmpty()) {
            throw new InvalidAuctionException("Cannot delete an auction with bids");
        }

        auctionRepository.deleteById(auctionId);
        logger.info("Deleted auction ID: {}", auctionId);
    }
}