package com.marketplace.controller;

import com.marketplace.dto.*;
import com.marketplace.entity.Product;
import com.marketplace.entity.User;
import com.marketplace.service.ProductService;
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
 * REST Controller for Product operations
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final UserService userService;
    private final EntityMapper entityMapper;

    private Long getUserId(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    /**
     * Get all products
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getAllProducts() {
        log.info("Fetching all products");
        
        List<ProductResponseDTO> products = productService.findAll().stream()
                .map(entityMapper::toProductResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", products));
    }

    /**
     * Get all available products
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getAllAvailableProducts() {
        log.info("Fetching all available products");
        
        List<ProductResponseDTO> products = productService.findAllAvailable().stream()
                .map(entityMapper::toProductResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Available products retrieved successfully", products));
    }

    /**
     * Get product by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductById(@PathVariable Long id) {
        log.info("Fetching product with id: {}", id);
        
        Product product = productService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        return ResponseEntity.ok(ApiResponse.success(entityMapper.toProductResponseDTO(product)));
    }

    /**
     * Get products by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getProductsByCategory(@PathVariable String category) {
        log.info("Fetching products by category: {}", category);
        
        List<ProductResponseDTO> products = productService.findByCategory(category).stream()
                .map(entityMapper::toProductResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", products));
    }

    /**
     * Get products by seller
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getProductsBySeller(@PathVariable Long sellerId) {
        log.info("Fetching products by seller id: {}", sellerId);
        
        List<ProductResponseDTO> products = productService.findBySellerId(sellerId).stream()
                .map(entityMapper::toProductResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", products));
    }

    /**
     * Search products by name
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> searchProducts(@RequestParam String name) {
        log.info("Searching products by name: {}", name);
        
        List<ProductResponseDTO> products = productService.searchByName(name).stream()
                .map(entityMapper::toProductResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved successfully", products));
    }

    /**
     * Get products within price range
     */
    @GetMapping("/price-range")
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        
        log.info("Fetching products between {} and {}", minPrice, maxPrice);
        
        List<ProductResponseDTO> products = productService.findByPriceRange(minPrice, maxPrice).stream()
                .map(entityMapper::toProductResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", products));
    }

    /**
     * Create a new product (SELLER or ADMIN)
     */
    @PostMapping
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(
            @Valid @RequestBody ProductCreateDTO createDTO,
            Authentication authentication) {
        
        // Always use authenticated user as seller
        Long sellerId = getUserId(authentication);
        log.info("Creating new product: {} for seller: {}", createDTO.getName(), sellerId);
        
        Product product = entityMapper.toProductEntity(createDTO);
        Product createdProduct = productService.createProduct(product, sellerId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", 
                        entityMapper.toProductResponseDTO(createdProduct)));
    }

    /**
     * Update product (Owner SELLER or ADMIN)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @productSecurity.isOwner(authentication, #id)")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO updateDTO) {
        
        log.info("Updating product with id: {}", id);
        
        Product productUpdate = entityMapper.toProductEntity(updateDTO);
        Product updatedProduct = productService.updateProduct(id, productUpdate);
        
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", 
                entityMapper.toProductResponseDTO(updatedProduct)));
    }

    /**
     * Delete product (Owner SELLER or ADMIN)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @productSecurity.isOwner(authentication, #id)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        log.info("Deleting product with id: {}", id);
        
        productService.deleteProduct(id);
        
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }

    /**
     * Update product stock (Owner SELLER or ADMIN)
     */
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN') or @productSecurity.isOwner(authentication, #id)")
    public ResponseEntity<ApiResponse<Void>> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        
        log.info("Updating stock for product id: {} to {}", id, quantity);
        
        productService.updateStock(id, quantity);
        
        return ResponseEntity.ok(ApiResponse.success("Stock updated successfully", null));
    }

    /**
     * Toggle product availability (Owner SELLER or ADMIN)
     */
    @PatchMapping("/{id}/toggle-availability")
    @PreAuthorize("hasRole('ADMIN') or @productSecurity.isOwner(authentication, #id)")
    public ResponseEntity<ApiResponse<Void>> toggleAvailability(@PathVariable Long id) {
        log.info("Toggling availability for product id: {}", id);
        
        productService.toggleAvailability(id);
        
        return ResponseEntity.ok(ApiResponse.success("Product availability toggled successfully", null));
    }
    
    /**
     * Get current seller's products
     */
    @GetMapping("/seller/my-products")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getMyProducts(
            org.springframework.security.core.Authentication authentication) {
        
        String username = authentication.getName();
        log.info("Fetching products for seller: {}", username);
        
        // Get user by username and then their products
        List<ProductResponseDTO> products = productService.findBySellerUsername(username).stream()
                .map(entityMapper::toProductResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Your products retrieved successfully", products));
    }
}
