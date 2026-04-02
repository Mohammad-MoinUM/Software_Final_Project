package com.marketplace.controller;

import com.marketplace.dto.*;
import com.marketplace.entity.Order;
import com.marketplace.entity.OrderStatus;
import com.marketplace.entity.User;
import com.marketplace.service.OrderService;
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Order operations
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final EntityMapper entityMapper;

    private Long getUserId(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    /**
     * Get all orders (ADMIN only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getAllOrders() {
        log.info("Fetching all orders");
        
        List<OrderResponseDTO> orders = orderService.findAll().stream()
                .map(entityMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders));
    }

    /**
     * Get order by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isOwner(authentication, #id)")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrderById(@PathVariable Long id) {
        log.info("Fetching order with id: {}", id);
        
        Order order = orderService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
        
        return ResponseEntity.ok(ApiResponse.success(entityMapper.toOrderResponseDTO(order)));
    }

    /**
     * Get orders by buyer
     */
    @GetMapping("/buyer/{buyerId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwner(authentication, #buyerId)")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getOrdersByBuyer(@PathVariable Long buyerId) {
        log.info("Fetching orders by buyer id: {}", buyerId);
        
        List<OrderResponseDTO> orders = orderService.findByBuyerId(buyerId).stream()
                .map(entityMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders));
    }

    /**
     * Get orders by seller (products sold by them)
     */
    @GetMapping("/seller/{sellerId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwner(authentication, #sellerId)")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getOrdersBySeller(@PathVariable Long sellerId) {
        log.info("Fetching orders by seller id: {}", sellerId);
        
        List<OrderResponseDTO> orders = orderService.findOrdersBySellerId(sellerId).stream()
                .map(entityMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders));
    }

    /**
     * Get orders by status (ADMIN only)
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getOrdersByStatus(@PathVariable String status) {
        log.info("Fetching orders by status: {}", status);
        
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        List<OrderResponseDTO> orders = orderService.findByStatus(orderStatus).stream()
                .map(entityMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders));
    }

    /**
     * Create a new order (authenticated user)
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(
            @Valid @RequestBody OrderCreateDTO createDTO,
            Authentication authentication) {
        
        // Use authenticated user's ID, ignore any buyerId from request body
        Long buyerId = getUserId(authentication);
        log.info("Creating new order for buyer id: {}", buyerId);
        
        Order order = orderService.createOrder(buyerId, createDTO.getProductIds());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", 
                        entityMapper.toOrderResponseDTO(order)));
    }

    /**
     * Update order status (ADMIN or SELLER who owns the products)
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        log.info("Updating order {} to status: {}", id, status);
        
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        Order updatedOrder = orderService.updateOrderStatus(id, orderStatus);
        
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", 
                entityMapper.toOrderResponseDTO(updatedOrder)));
    }

    /**
     * Cancel an order
     */
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isOwner(authentication, #id)")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> cancelOrder(@PathVariable Long id) {
        log.info("Cancelling order with id: {}", id);
        
        Order cancelledOrder = orderService.cancelOrder(id);
        
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", 
                entityMapper.toOrderResponseDTO(cancelledOrder)));
    }

    /**
     * Delete an order (ADMIN only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long id) {
        log.info("Deleting order with id: {}", id);
        
        orderService.deleteOrder(id);
        
        return ResponseEntity.ok(ApiResponse.success("Order deleted successfully", null));
    }
    
    /**
     * Get current user's orders
     */
    @GetMapping("/my-orders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getMyOrders(
            org.springframework.security.core.Authentication authentication) {
        
        String username = authentication.getName();
        log.info("Fetching orders for user: {}", username);
        
        List<OrderResponseDTO> orders = orderService.findByUsername(username).stream()
                .map(entityMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Your orders retrieved successfully", orders));
    }
}
