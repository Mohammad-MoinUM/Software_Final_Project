package com.marketplace.security;

import com.marketplace.entity.Product;
import com.marketplace.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Security helper for product ownership checks
 */
@Component("productSecurity")
@RequiredArgsConstructor
public class ProductSecurity {

    private final ProductRepository productRepository;

    /**
     * Check if the authenticated user is the seller of the product
     */
    public boolean isOwner(Authentication authentication, Long productId) {
        if (authentication == null || productId == null) {
            return false;
        }

        String username = authentication.getName();
        Product product = productRepository.findById(productId).orElse(null);
        
        if (product == null || product.getSeller() == null) {
            return false;
        }

        return product.getSeller().getUsername().equals(username);
    }
}
