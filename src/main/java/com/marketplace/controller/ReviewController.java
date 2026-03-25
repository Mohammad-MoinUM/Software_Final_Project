package com.marketplace.controller;

import com.marketplace.dto.*;
import com.marketplace.entity.Review;
import com.marketplace.service.ReviewService;
import com.marketplace.util.EntityMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Review operations
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    private final EntityMapper entityMapper;

    /**
     * Get reviews by product ID
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ReviewResponseDTO>>> getReviewsByProductId(@PathVariable Long productId) {
        log.info("Fetching reviews for product {}", productId);
        
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByProductId(productId).stream()
                .map(entityMapper::toReviewResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Reviews retrieved successfully", reviews));
    }

    /**
     * Get average rating for product
     */
    @GetMapping("/product/{productId}/average-rating")
    public ResponseEntity<ApiResponse<Double>> getAverageRating(@PathVariable Long productId) {
        Double avgRating = reviewService.getAverageRating(productId);
        return ResponseEntity.ok(ApiResponse.success("Average rating retrieved", avgRating));
    }

    /**
     * Get reviews by user (authenticated user's reviews)
     */
    @GetMapping("/my-reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ReviewResponseDTO>>> getMyReviews(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByUserId(userId).stream()
                .map(entityMapper::toReviewResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Your reviews retrieved successfully", reviews));
    }

    /**
     * Create a review
     */
    @PostMapping
    @PreAuthorize("hasRole('BUYER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> createReview(
            @Valid @RequestBody ReviewCreateDTO createDTO,
            Authentication authentication) {
        
        Long userId = Long.parseLong(authentication.getName());
        log.info("Creating review by user {}", userId);
        
        Review review = entityMapper.toReviewEntity(createDTO);
        Review createdReview = reviewService.createReview(review, userId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review created successfully", 
                        entityMapper.toReviewResponseDTO(createdReview)));
    }

    /**
     * Update a review
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewCreateDTO updateDTO,
            Authentication authentication) {
        
        Long userId = Long.parseLong(authentication.getName());
        log.info("Updating review {} by user {}", id, userId);
        
        Review review = entityMapper.toReviewEntity(updateDTO);
        Review updatedReview = reviewService.updateReview(id, review, userId);
        
        return ResponseEntity.ok(ApiResponse.success("Review updated successfully", 
                entityMapper.toReviewResponseDTO(updatedReview)));
    }

    /**
     * Delete a review
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long userId = Long.parseLong(authentication.getName());
        log.info("Deleting review {} by user {}", id, userId);
        
        reviewService.deleteReview(id, userId);
        
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully", null));
    }
}
