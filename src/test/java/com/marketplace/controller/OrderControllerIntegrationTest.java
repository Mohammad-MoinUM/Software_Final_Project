package com.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.dto.OrderCreateDTO;
import com.marketplace.entity.*;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for OrderController
 */
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired // Inject MockMvc for performing HTTP requests in tests
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testBuyer;
    private User testSeller;
    private Product testProduct;
    private Order testOrder;

    @BeforeEach // Set up test data before each test
    void setUp() {
        // Clear database
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Create roles
        Role buyerRole = roleRepository.save(Role.builder()
                .name(RoleType.BUYER)
                .description("Buyer role")
                .build());

        Role sellerRole = roleRepository.save(Role.builder()
                .name(RoleType.SELLER)
                .description("Seller role")
                .build());

        // Create test buyer
        testBuyer = User.builder()
                .username("buyer")
                .email("buyer@example.com")
                .password(passwordEncoder.encode("password123"))
                .fullName("Test Buyer")
                .enabled(true)
                .roles(new HashSet<>(Arrays.asList(buyerRole)))
                .build();
        userRepository.save(testBuyer);

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
                .available(true)
                .seller(testSeller)
                .build();
        productRepository.save(testProduct);

        // Create test order
        testOrder = Order.builder()
                .orderNumber("ORD-TEST-001")
                .buyer(testBuyer)
                .products(new HashSet<>(Arrays.asList(testProduct)))
                .totalAmount(new BigDecimal("99.99"))
                .status(OrderStatus.PENDING)
                .build();
        orderRepository.save(testOrder);
    }

    @Test // Test that an admin can retrieve all orders successfully
    @WithMockUser(roles = "ADMIN")
    void testGetAllOrders_AsAdmin_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "BUYER")
    void testGetAllOrders_AsNonAdmin_Forbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isForbidden());
    }

    @Test // Test that a buyer can retrieve their own order successfully
    @WithMockUser(username = "buyer", roles = "BUYER")
    void testGetOrderById_AsOwner_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/orders/" + testOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").value("ORD-TEST-001"));
    }

    @Test // Test that a buyer can retrieve their own orders successfully       
    @WithMockUser(username = "buyer", roles = "BUYER")
    void testGetOrdersByBuyer_AsOwner_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/orders/buyer/" + testBuyer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test // Test that an authenticated buyer can create an order successfully
    @WithMockUser(username = "buyer", roles = "BUYER")
    void testCreateOrder_AsAuthenticatedUser_Success() throws Exception {
        // Arrange
        OrderCreateDTO createDTO = OrderCreateDTO.builder()
                .buyerId(testBuyer.getId())
                .productIds(Set.of(testProduct.getId()))
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").exists());
    }

    @Test
    void testCreateOrder_AsUnauthenticated_Unauthorized() throws Exception {
        // Arrange
        OrderCreateDTO createDTO = OrderCreateDTO.builder()
                .buyerId(testBuyer.getId())
                .productIds(Set.of(testProduct.getId()))
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test // Test that an admin can retrieve orders by status successfully
    @WithMockUser(roles = "ADMIN")
    void testGetOrdersByStatus_AsAdmin_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/orders/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test // Test that a buyer can cancel their own order successfully
    @WithMockUser(username = "buyer", roles = "BUYER")
    void testCancelOrder_AsOwner_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/api/orders/" + testOrder.getId() + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test // Test that an admin can delete an order successfully
    @WithMockUser(roles = "ADMIN")
    void testDeleteOrder_AsAdmin_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/orders/" + testOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test // Test that a seller can retrieve their own orders successfully      
    @WithMockUser(username = "seller", roles = "SELLER")
    void testGetOrdersBySeller_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/orders/seller/" + testSeller.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
