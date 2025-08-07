package com.webapp.realtimeauctionbackend.controllers;

import com.webapp.realtimeauctionbackend.models.Review;
import com.webapp.realtimeauctionbackend.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Review>> getUserReviews(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(reviewService.getUserReviews(userId, pageable));
    }

    @GetMapping("/user/{userId}/rating")
    public ResponseEntity<Double> getUserAverageRating(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getUserAverageRating(userId));
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> getUserReviewCount(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getUserReviewCount(userId));
    }

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        return ResponseEntity.ok(reviewService.createReview(review));
    }
} 