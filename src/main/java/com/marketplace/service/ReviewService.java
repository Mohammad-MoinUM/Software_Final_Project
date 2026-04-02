package com.marketplace.service;

import com.marketplace.entity.Review;
import com.marketplace.entity.Product;
import com.marketplace.entity.User;
import com.marketplace.repository.ReviewRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Review operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Create a new review
     */
    public Review createReview(Review review, Long userId) {
        log.info("Creating review for product {} by user {}", review.getProduct().getId(), userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Product product = productRepository.findById(review.getProduct().getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Check if user already reviewed this product
        if (reviewRepository.existsByProductIdAndUserId(product.getId(), userId)) {
            throw new IllegalStateException("You have already reviewed this product");
        }

        review.setUser(user);
        review.setProduct(product);

        return reviewRepository.save(review);
    }

    /**
     * Get reviews by product ID
     */
    @Transactional(readOnly = true)
    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    /**
     * Get reviews by user ID
     */
    @Transactional(readOnly = true)
    public List<Review> getReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    /**
     * Get average rating for a product
     */
    @Transactional(readOnly = true)
    public Double getAverageRating(Long productId) {
        Double avg = reviewRepository.getAverageRatingByProductId(productId);
        return avg != null ? avg : 0.0;
    }

    /**
     * Get review count for a product
     */
    @Transactional(readOnly = true)
    public long getReviewCount(Long productId) {
        return reviewRepository.countByProductId(productId);
    }

    /**
     * Update a review
     */
    public Review updateReview(Long reviewId, Review updatedReview, Long userId) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        if (!existingReview.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You can only update your own reviews");
        }

        existingReview.setRating(updatedReview.getRating());
        existingReview.setComment(updatedReview.getComment());

        return reviewRepository.save(existingReview);
    }

    /**
     * Delete a review
     */
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
    }

    /**
     * Find review by ID
     */
    @Transactional(readOnly = true)
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }
}
