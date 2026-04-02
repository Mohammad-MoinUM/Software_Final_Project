package com.marketplace.controller;

import com.marketplace.dto.*;
import com.marketplace.entity.CartItem;
import com.marketplace.entity.User;
import com.marketplace.service.CartService;
import com.marketplace.service.UserService;
import com.marketplace.util.EntityMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Shopping Cart operations
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    private final EntityMapper entityMapper;

    private Long getUserId(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    /**
     * Get cart items for current user
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<CartItemResponseDTO>>> getCart(Authentication authentication) {
        Long userId = getUserId(authentication);
        log.info("Fetching cart for user {}", userId);
        
        List<CartItemResponseDTO> cartItems = cartService.getCartItems(userId).stream()
                .map(entityMapper::toCartItemResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved successfully", cartItems));
    }

    /**
     * Get cart total
     */
    @GetMapping("/total")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BigDecimal>> getCartTotal(Authentication authentication) {
        Long userId = getUserId(authentication);
        BigDecimal total = cartService.getCartTotal(userId);
        
        return ResponseEntity.ok(ApiResponse.success("Cart total calculated", total));
    }

    /**
     * Get cart item count
     */
    @GetMapping("/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Long>> getCartCount(Authentication authentication) {
        Long userId = getUserId(authentication);
        long count = cartService.getCartItemCount(userId);
        
        return ResponseEntity.ok(ApiResponse.success("Cart count retrieved", count));
    }

    /**
     * Add item to cart
     */
    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CartItemResponseDTO>> addToCart(
            @Valid @RequestBody CartItemCreateDTO createDTO,
            Authentication authentication) {
        
        Long userId = getUserId(authentication);
        log.info("Adding product {} to cart for user {}", createDTO.getProductId(), userId);
        
        CartItem cartItem = cartService.addToCart(userId, createDTO.getProductId(), createDTO.getQuantity());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Item added to cart", 
                        entityMapper.toCartItemResponseDTO(cartItem)));
    }

    /**
     * Update cart item quantity
     */
    @PutMapping("/{cartItemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CartItemResponseDTO>> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity,
            Authentication authentication) {
        
        Long userId = getUserId(authentication);
        log.info("Updating cart item {} quantity to {}", cartItemId, quantity);
        
        CartItem cartItem = cartService.updateCartItemQuantity(cartItemId, quantity, userId);
        
        return ResponseEntity.ok(ApiResponse.success("Cart item updated", 
                entityMapper.toCartItemResponseDTO(cartItem)));
    }

    /**
     * Remove item from cart
     */
    @DeleteMapping("/{cartItemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @PathVariable Long cartItemId,
            Authentication authentication) {
        
        Long userId = getUserId(authentication);
        log.info("Removing cart item {} for user {}", cartItemId, userId);
        
        cartService.removeFromCart(cartItemId, userId);
        
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", null));
    }

    /**
     * Clear cart
     */
    @DeleteMapping("/clear")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> clearCart(Authentication authentication) {
        Long userId = getUserId(authentication);
        log.info("Clearing cart for user {}", userId);
        
        cartService.clearCart(userId);
        
        return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully", null));
    }
}
