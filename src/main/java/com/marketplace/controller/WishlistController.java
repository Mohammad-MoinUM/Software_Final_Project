package com.marketplace.controller;

import com.marketplace.dto.ApiResponse;
import com.marketplace.dto.WishlistResponseDTO;
import com.marketplace.entity.User;
import com.marketplace.entity.Wishlist;
import com.marketplace.service.UserService;
import com.marketplace.service.WishlistService;
import com.marketplace.util.EntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Wishlist operations
 */
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Slf4j
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserService userService;
    private final EntityMapper entityMapper;

    private Long getUserId(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    /**
     * Get user's wishlist
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<WishlistResponseDTO>> getWishlist(Authentication authentication) {
        Long userId = getUserId(authentication);
        log.info("Fetching wishlist for user {}", userId);
        
        Wishlist wishlist = wishlistService.getOrCreateWishlist(userId);
        
        return ResponseEntity.ok(ApiResponse.success("Wishlist retrieved successfully", 
                entityMapper.toWishlistResponseDTO(wishlist)));
    }

    /**
     * Add product to wishlist
     */
    @PostMapping("/add/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<WishlistResponseDTO>> addToWishlist(
            @PathVariable Long productId,
            Authentication authentication) {
        
        Long userId = getUserId(authentication);
        log.info("Adding product {} to wishlist for user {}", productId, userId);
        
        Wishlist wishlist = wishlistService.addProductToWishlist(userId, productId);
        
        return ResponseEntity.ok(ApiResponse.success("Product added to wishlist", 
                entityMapper.toWishlistResponseDTO(wishlist)));
    }

    /**
     * Remove product from wishlist
     */
    @DeleteMapping("/remove/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<WishlistResponseDTO>> removeFromWishlist(
            @PathVariable Long productId,
            Authentication authentication) {
        
        Long userId = getUserId(authentication);
        log.info("Removing product {} from wishlist for user {}", productId, userId);
        
        Wishlist wishlist = wishlistService.removeProductFromWishlist(userId, productId);
        
        return ResponseEntity.ok(ApiResponse.success("Product removed from wishlist", 
                entityMapper.toWishlistResponseDTO(wishlist)));
    }

    /**
     * Check if product is in wishlist
     */
    @GetMapping("/check/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Boolean>> checkInWishlist(
            @PathVariable Long productId,
            Authentication authentication) {
        
        Long userId = getUserId(authentication);
        boolean inWishlist = wishlistService.isProductInWishlist(userId, productId);
        
        return ResponseEntity.ok(ApiResponse.success("Check completed", inWishlist));
    }

    /**
     * Clear wishlist
     */
    @DeleteMapping("/clear")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> clearWishlist(Authentication authentication) {
        Long userId = getUserId(authentication);
        log.info("Clearing wishlist for user {}", userId);
        
        wishlistService.clearWishlist(userId);
        
        return ResponseEntity.ok(ApiResponse.success("Wishlist cleared successfully", null));
    }
}
