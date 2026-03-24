package com.marketplace.repository;

import com.marketplace.entity.Wishlist;
import com.marketplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Wishlist entity
 */
@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    /**
     * Find wishlist by user
     */
    Optional<Wishlist> findByUser(User user);

    /**
     * Find wishlist by user ID
     */
    Optional<Wishlist> findByUserId(Long userId);

    /**
     * Check if wishlist exists for user
     */
    boolean existsByUserId(Long userId);
}
