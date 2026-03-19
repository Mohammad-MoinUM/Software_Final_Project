package com.marketplace.service;

import com.marketplace.entity.Order;
import com.marketplace.entity.OrderStatus;
import com.marketplace.entity.Product;
import com.marketplace.entity.User;
import com.marketplace.repository.OrderRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service layer for Order operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    /**
     * Find order by ID
     */
    public Optional<Order> findById(Long id) {
        log.debug("Finding order by id: {}", id);
        return orderRepository.findById(id);
    }

    /**
     * Get all orders
     */
    public List<Order> findAll() {
        log.debug("Fetching all orders");
        return orderRepository.findAll();
    }

    /**
     * Get orders by buyer
     */
    public List<Order> findByBuyerId(Long buyerId) {
        log.debug("Finding orders by buyer id: {}", buyerId);
        return orderRepository.findByBuyerId(buyerId);
    }

    /**
     * Get orders by status
     */
    public List<Order> findByStatus(OrderStatus status) {
        log.debug("Finding orders by status: {}", status);
        return orderRepository.findByStatus(status);
    }

    /**
     * Create a new order
     */
    public Order createOrder(Long buyerId, Set<Long> productIds) {
        log.info("Creating new order for buyer id: {}", buyerId);
        
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found with id: " + buyerId));
        
        Set<Product> products = new HashSet<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (Long productId : productIds) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
            
            if (!product.getAvailable()) {
                throw new IllegalStateException("Product not available: " + product.getName());
            }
            
            if (product.getStockQuantity() <= 0) {
                throw new IllegalStateException("Product out of stock: " + product.getName());
            }
            
            products.add(product);
            totalAmount = totalAmount.add(product.getPrice());
        }
        
        // Generate unique order number
        String orderNumber = "ORD-" + System.currentTimeMillis() + "-" + buyerId;
        
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .buyer(buyer)
                .products(products)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .build();
        
        Order savedOrder = orderRepository.save(order);
        
        // Decrease stock for ordered products
        for (Product product : products) {
            productService.decreaseStock(product.getId(), 1);
        }
        
        return savedOrder;
    }

    /**
     * Update order status
     */
    public Order updateOrderStatus(Long id, OrderStatus status) {
        log.info("Updating order {} to status: {}", id, status);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
        
        order.setStatus(status);
        return orderRepository.save(order);
    }

    /**
     * Cancel an order
     */
    public Order cancelOrder(Long id) {
        log.info("Cancelling order with id: {}", id);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
        
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel order with status: " + order.getStatus());
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        
        // Restore stock for cancelled orders
        for (Product product : order.getProducts()) {
            Product p = productRepository.findById(product.getId()).orElse(null);
            if (p != null) {
                p.setStockQuantity(p.getStockQuantity() + 1);
                p.setAvailable(true);
                productRepository.save(p);
            }
        }
        
        return orderRepository.save(order);
    }

    /**
     * Delete an order (admin only)
     */
    public void deleteOrder(Long id) {
        log.info("Deleting order with id: {}", id);
        
        if (!orderRepository.existsById(id)) {
            throw new IllegalArgumentException("Order not found with id: " + id);
        }
        
        orderRepository.deleteById(id);
    }

    /**
     * Get orders for a specific seller (products sold by them)
     */
    public List<Order> findOrdersBySellerId(Long sellerId) {
        log.debug("Finding orders containing products from seller id: {}", sellerId);
        return orderRepository.findByProductsSellerId(sellerId);
    }
    
    /**
     * Get orders by username (buyer or seller)
     */
    public List<Order> findByUsername(String username) {
        log.debug("Finding orders for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
        
        // Try to get as buyer first
        List<Order> orders = orderRepository.findByBuyerId(user.getId());
        
        // If no buyer orders and user is seller, get seller orders
        if (orders.isEmpty()) {
            orders = orderRepository.findByProductsSellerId(user.getId());
        }
        
        return orders;
    }
}
