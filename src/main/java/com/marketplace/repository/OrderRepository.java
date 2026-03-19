package com.marketplace.repository;

import com.marketplace.entity.Order;
import com.marketplace.entity.OrderStatus;
import com.marketplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entity
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find order by order number
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Find all orders by buyer
     */
    List<Order> findByBuyer(User buyer);

    /**
     * Find orders by buyer ID
     */
    List<Order> findByBuyerId(Long buyerId);

    /**
     * Find orders by status
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Find orders by buyer and status
     */
    List<Order> findByBuyerIdAndStatus(Long buyerId, OrderStatus status);

    /**
     * Find orders created between dates
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find orders by buyer ID ordered by creation date descending
     */
    List<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);

    /**
     * Count orders by status
     */
    long countByStatus(OrderStatus status);

    /**
     * Find orders containing products from a specific seller
     */
    @Query("SELECT DISTINCT o FROM Order o JOIN o.products p WHERE p.seller.id = :sellerId")
    List<Order> findByProductsSellerId(@Param("sellerId") Long sellerId);

}
