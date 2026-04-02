package com.marketplace.service;

import com.marketplace.entity.Product;
import com.marketplace.entity.User;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Product operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Find product by ID
     */
    public Optional<Product> findById(Long id) {
        log.debug("Finding product by id: {}", id);
        return productRepository.findById(id);
    }

    /**
     * Get all products
     */
    public List<Product> findAll() {
        log.debug("Fetching all products");
        return productRepository.findAll();
    }

    /**
     * Get all available products
     */
    public List<Product> findAllAvailable() {
        log.debug("Fetching all available products");
        return productRepository.findByAvailableTrue();
    }

    /**
     * Get products by category
     */
    public List<Product> findByCategory(String category) {
        log.debug("Finding products by category: {}", category);
        return productRepository.findByCategory(category);
    }

    /**
     * Get products by seller
     */
    public List<Product> findBySellerId(Long sellerId) {
        log.debug("Finding products by seller id: {}", sellerId);
        return productRepository.findBySellerId(sellerId);
    }

    /**
     * Search products by name
     */
    public List<Product> searchByName(String name) {
        log.debug("Searching products by name: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Get products within price range
     */
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("Finding products between {} and {}", minPrice, maxPrice);
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    /**
     * Create a new product
     */
    public Product createProduct(Product product, Long sellerId) {
        log.info("Creating new product: {}", product.getName());
        
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found with id: " + sellerId));
        
        product.setSeller(seller);
        return productRepository.save(product);
    }

    /**
     * Update existing product
     */
    public Product updateProduct(Long id, Product updatedProduct) {
        log.info("Updating product with id: {}", id);
        
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        // Update fields
        if (updatedProduct.getName() != null) {
            existingProduct.setName(updatedProduct.getName());
        }
        
        if (updatedProduct.getDescription() != null) {
            existingProduct.setDescription(updatedProduct.getDescription());
        }
        
        if (updatedProduct.getPrice() != null) {
            existingProduct.setPrice(updatedProduct.getPrice());
        }
        
        if (updatedProduct.getStockQuantity() != null) {
            existingProduct.setStockQuantity(updatedProduct.getStockQuantity());
        }
        
        if (updatedProduct.getCategory() != null) {
            existingProduct.setCategory(updatedProduct.getCategory());
        }
        
        if (updatedProduct.getImageUrl() != null) {
            existingProduct.setImageUrl(updatedProduct.getImageUrl());
        }
        
        if (updatedProduct.getAvailable() != null) {
            existingProduct.setAvailable(updatedProduct.getAvailable());
        }

        return productRepository.save(existingProduct);
    }

    /**
     * Delete product
     */
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        
        productRepository.deleteById(id);
    }

    /**
     * Update product stock
     */
    public void updateStock(Long id, Integer quantity) {
        log.info("Updating stock for product id: {} to {}", id, quantity);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        product.setStockQuantity(quantity);
        
        // Mark as unavailable if out of stock
        if (quantity <= 0) {
            product.setAvailable(false);
        }
        
        productRepository.save(product);
    }

    /**
     * Decrease stock (for orders)
     */
    public void decreaseStock(Long id, Integer quantity) {
        log.info("Decreasing stock for product id: {} by {}", id, quantity);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        if (product.getStockQuantity() < quantity) {
            throw new IllegalStateException("Insufficient stock for product: " + product.getName());
        }
        
        product.setStockQuantity(product.getStockQuantity() - quantity);
        
        // Mark as unavailable if out of stock
        if (product.getStockQuantity() <= 0) {
            product.setAvailable(false);
        }
        
        productRepository.save(product);
    }

    /**
     * Toggle product availability
     */
    public void toggleAvailability(Long id) {
        log.info("Toggling availability for product id: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        product.setAvailable(!product.getAvailable());
        productRepository.save(product);
    }
    
    /**
     * Get products by seller username
     */
    public List<Product> findBySellerUsername(String username) {
        log.debug("Finding products by seller username: {}", username);
        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found with username: " + username));
        return productRepository.findBySellerId(seller.getId());
    }
}
