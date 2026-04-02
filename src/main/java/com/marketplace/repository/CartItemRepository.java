package com.marketplace.repository;

import com.marketplace.entity.CartItem;
import com.marketplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CartItem entity
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Find all cart items for a user
     */
    List<CartItem> findByUser(User user);

    /**
     * Find all cart items by user ID
     */
    List<CartItem> findByUserId(Long userId);

    /**
     * Find cart item by user and product
     */
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    /**
     * Delete all cart items for a user
     */
    void deleteByUserId(Long userId);

    /**
     * Count cart items for a user
     */
    long countByUserId(Long userId);

    /**
     * Get total cart value for a user
     */
    @Query("SELECT SUM(ci.quantity * ci.product.price) FROM CartItem ci WHERE ci.user.id = :userId")
    Double getTotalCartValue(@Param("userId") Long userId);
}
