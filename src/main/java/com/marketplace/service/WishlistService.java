package com.marketplace.service;

import com.marketplace.entity.Wishlist;
import com.marketplace.entity.Product;
import com.marketplace.entity.User;
import com.marketplace.repository.WishlistRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for Wishlist operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Get or create wishlist for user
     */
    public Wishlist getOrCreateWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("User not found"));
                    
                    Wishlist wishlist = Wishlist.builder()
                            .user(user)
                            .build();
                    return wishlistRepository.save(wishlist);
                });
    }

    /**
     * Add product to wishlist
     */
    public Wishlist addProductToWishlist(Long userId, Long productId) {
        log.info("Adding product {} to wishlist for user {}", productId, userId);

        Wishlist wishlist = getOrCreateWishlist(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        wishlist.getProducts().add(product);
        return wishlistRepository.save(wishlist);
    }

    /**
     * Remove product from wishlist
     */
    public Wishlist removeProductFromWishlist(Long userId, Long productId) {
        log.info("Removing product {} from wishlist for user {}", productId, userId);

        Wishlist wishlist = getOrCreateWishlist(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        wishlist.getProducts().remove(product);
        return wishlistRepository.save(wishlist);
    }

    /**
     * Get wishlist by user ID
     */
    @Transactional(readOnly = true)
    public Optional<Wishlist> getWishlistByUserId(Long userId) {
        return wishlistRepository.findByUserId(userId);
    }

    /**
     * Check if product is in wishlist
     */
    @Transactional(readOnly = true)
    public boolean isProductInWishlist(Long userId, Long productId) {
        Optional<Wishlist> wishlist = wishlistRepository.findByUserId(userId);
        if (wishlist.isEmpty()) {
            return false;
        }
        return wishlist.get().getProducts().stream()
                .anyMatch(p -> p.getId().equals(productId));
    }

    /**
     * Clear wishlist
     */
    public void clearWishlist(Long userId) {
        Optional<Wishlist> wishlist = wishlistRepository.findByUserId(userId);
        wishlist.ifPresent(w -> {
            w.getProducts().clear();
            wishlistRepository.save(w);
        });
    }
}
