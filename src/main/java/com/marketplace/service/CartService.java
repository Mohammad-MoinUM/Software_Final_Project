package com.marketplace.service;

import com.marketplace.entity.CartItem;
import com.marketplace.entity.Product;
import com.marketplace.entity.User;
import com.marketplace.repository.CartItemRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service class for CartItem operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Add item to cart
     */
    public CartItem addToCart(Long userId, Long productId, Integer quantity) {
        log.info("Adding product {} to cart for user {} with quantity {}", productId, userId, quantity);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Check stock availability
        if (product.getStockQuantity() < quantity) {
            throw new IllegalStateException("Insufficient stock. Available: " + product.getStockQuantity());
        }

        // Check if item already in cart
        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
        
        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            
            if (product.getStockQuantity() < newQuantity) {
                throw new IllegalStateException("Insufficient stock for total quantity");
            }
            
            cartItem.setQuantity(newQuantity);
            return cartItemRepository.save(cartItem);
        }

        CartItem cartItem = CartItem.builder()
                .user(user)
                .product(product)
                .quantity(quantity)
                .build();

        return cartItemRepository.save(cartItem);
    }

    /**
     * Update cart item quantity
     */
    public CartItem updateCartItemQuantity(Long cartItemId, Integer quantity, Long userId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!cartItem.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Unauthorized access to cart item");
        }

        if (cartItem.getProduct().getStockQuantity() < quantity) {
            throw new IllegalStateException("Insufficient stock");
        }

        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    /**
     * Remove item from cart
     */
    public void removeFromCart(Long cartItemId, Long userId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!cartItem.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Unauthorized access to cart item");
        }

        cartItemRepository.delete(cartItem);
    }

    /**
     * Get cart items for user
     */
    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    /**
     * Clear cart
     */
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    /**
     * Get cart total
     */
    @Transactional(readOnly = true)
    public BigDecimal getCartTotal(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        return items.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get cart item count
     */
    @Transactional(readOnly = true)
    public long getCartItemCount(Long userId) {
        return cartItemRepository.countByUserId(userId);
    }
}
