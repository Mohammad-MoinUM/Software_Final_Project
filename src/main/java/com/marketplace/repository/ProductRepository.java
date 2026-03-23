package com.marketplace.repository;

import com.marketplace.entity.Product;
import com.marketplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for Product entity
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find all products by seller
     */
    List<Product> findBySeller(User seller);

    /**
     * Find products by seller ID
     */
    List<Product> findBySellerId(Long sellerId);

    /**
     * Find all available products
     */
    List<Product> findByAvailableTrue();

    /**
     * Find products by category
     */
    List<Product> findByCategory(String category);

    /**
     * Find products by category and availability
     */
    List<Product> findByCategoryAndAvailableTrue(String category);

    /**
     * Find products by name containing (case-insensitive search)
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Find products by price range
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.available = true")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Find products by price between
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Find products with stock greater than specified quantity
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > :quantity AND p.available = true")
    List<Product> findInStockProducts(@Param("quantity") Integer quantity);

}
