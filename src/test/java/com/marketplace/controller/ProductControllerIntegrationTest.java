package com.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.dto.ProductCreateDTO;
import com.marketplace.entity.Product;
import com.marketplace.entity.Role;
import com.marketplace.entity.RoleType;
import com.marketplace.entity.User;
import com.marketplace.repository.OrderRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.RoleRepository;
import com.marketplace.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ProductController
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

        @Autowired
        private OrderRepository orderRepository;

        @Autowired
        private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testSeller;
    private Product testProduct;

    @BeforeEach
    @Transactional // Ensure each test runs in a transaction that rolls back after completion
    void setUp() {
        // Clear database
        jdbcTemplate.update("DELETE FROM order_products");
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        // Reuse roles from DataInitializer (find or create)
        Role sellerRole = roleRepository.findByName(RoleType.SELLER)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(RoleType.SELLER)
                        .description("Seller role")
                        .build()));

        // Create test seller
        testSeller = User.builder()
                .username("seller")
                .email("seller@example.com")
                .password(passwordEncoder.encode("password123"))
                .fullName("Test Seller")
                .enabled(true)
                .roles(new HashSet<>(Arrays.asList(sellerRole)))
                .build();
        userRepository.save(testSeller);

        // Create test product
        testProduct = Product.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(10)
                .category("Electronics")
                .available(true)
                .seller(testSeller)
                .build();
        productRepository.save(testProduct);
    }

    @Test // Test that all products can be retrieved successfully
    void testGetAllProducts_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test // Test that a product can be retrieved by ID successfully
    void testGetProductById_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/products/" + testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Test Product"))
                .andExpect(jsonPath("$.data.price").value(99.99));
    }

    @Test // Test that products can be retrieved by category successfully
    void testGetProductsByCategory_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/products/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testSearchProducts_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/products/search")
                .param("name", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test // Test that a seller can create a product successfully
        @WithMockUser(username = "seller", roles = "SELLER")
    void testCreateProduct_AsSeller_Success() throws Exception {
        // Arrange
        ProductCreateDTO createDTO = ProductCreateDTO.builder()
                .name("New Product")
                .description("New Description")
                .price(new BigDecimal("149.99"))
                .stockQuantity(5)
                .category("Books")
                .sellerId(testSeller.getId())
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("New Product"));
    }

    @Test // Test that a buyer cannot create a product (forbidden)
    @WithMockUser(roles = "BUYER")
    void testCreateProduct_AsBuyer_Forbidden() throws Exception {
        // Arrange
        ProductCreateDTO createDTO = ProductCreateDTO.builder()
                .name("New Product")
                .price(new BigDecimal("149.99"))
                .stockQuantity(5)
                .sellerId(testSeller.getId())
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isForbidden());
    }

    @Test // Test that all available products can be retrieved successfully
    void testGetAllAvailableProducts_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/products/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test // Test that products can be retrieved by seller successfully
    void testGetProductsBySeller_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/products/seller/" + testSeller.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test // Test that products can be retrieved by price range successfully
    void testGetProductsByPriceRange_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/products/price-range")
                .param("minPrice", "50")
                .param("maxPrice", "150"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
