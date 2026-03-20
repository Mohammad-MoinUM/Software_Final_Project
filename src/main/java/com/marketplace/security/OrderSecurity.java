package com.marketplace.security;

import com.marketplace.entity.Order;
import com.marketplace.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Security helper for order ownership checks
 */
@Component("orderSecurity")
@RequiredArgsConstructor
public class OrderSecurity {

    private final OrderRepository orderRepository;

    /**
     * Check if the authenticated user is the buyer of the order
     */
    public boolean isOwner(Authentication authentication, Long orderId) {
        if (authentication == null || orderId == null) {
            return false;
        }

        String username = authentication.getName();
        Order order = orderRepository.findById(orderId).orElse(null);
        
        if (order == null || order.getBuyer() == null) {
            return false;
        }

        return order.getBuyer().getUsername().equals(username);
    }
}
