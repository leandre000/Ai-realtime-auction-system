package com.webapp.realtimeauctionbackend.services;

import com.webapp.realtimeauctionbackend.models.Review;
import com.webapp.realtimeauctionbackend.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public Page<Review> getUserReviews(Long userId, Pageable pageable) {
        return reviewRepository.findByReviewedId(userId, pageable);
    }

    public Double getUserAverageRating(Long userId) {
        return reviewRepository.getAverageRating(userId);
    }

    public Long getUserReviewCount(Long userId) {
        return reviewRepository.getReviewCount(userId);
    }

    @Transactional
    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByReviewerAndReviewed(Long reviewerId, Long reviewedId) {
        return reviewRepository.findByReviewerIdAndReviewedId(reviewerId, reviewedId);
    }
} 