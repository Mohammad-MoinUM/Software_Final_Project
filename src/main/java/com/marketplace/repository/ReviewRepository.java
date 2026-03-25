package com.marketplace.repository;

import com.marketplace.entity.Review;
import com.marketplace.entity.Product;
import com.marketplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Review entity
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Find all reviews for a product
     */
    List<Review> findByProduct(Product product);

    /**
     * Find all reviews by product ID
     */
    List<Review> findByProductId(Long productId);

    /**
     * Find all reviews by a user
     */
    List<Review> findByUser(User user);

    /**
     * Find all reviews by user ID
     */
    List<Review> findByUserId(Long userId);

    /**
     * Check if user has reviewed a product
     */
    boolean existsByProductIdAndUserId(Long productId, Long userId);

    /**
     * Find review by product and user
     */
    Optional<Review> findByProductIdAndUserId(Long productId, Long userId);

    /**
     * Get average rating for a product
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double getAverageRatingByProductId(@Param("productId") Long productId);

    /**
     * Count reviews for a product
     */
    long countByProductId(Long productId);

    /**
     * Find verified purchase reviews
     */
    List<Review> findByProductIdAndVerifiedPurchaseTrue(Long productId);
}
