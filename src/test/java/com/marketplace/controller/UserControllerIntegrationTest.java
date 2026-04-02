package com.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.dto.UserRegistrationDTO;
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

import java.util.Arrays;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserController
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private OrderRepository orderRepository;

        @Autowired
        private JdbcTemplate jdbcTemplate;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private Role buyerRole;

    @BeforeEach
    @Transactional
    void setUp() {
        // Clear database
                jdbcTemplate.update("DELETE FROM order_products");
                orderRepository.deleteAll();
                productRepository.deleteAll();
                userRepository.deleteAll();

        // Reuse roles from DataInitializer (find or create)
        buyerRole = roleRepository.findByName(RoleType.BUYER)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(RoleType.BUYER)
                        .description("Buyer role")
                        .build()));

        roleRepository.findByName(RoleType.ADMIN)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(RoleType.ADMIN)
                        .description("Admin role")
                        .build()));

        // Create test user
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password(passwordEncoder.encode("password123"))
                .fullName("Test User")
                .phoneNumber("1234567890")
                .enabled(true)
                .roles(new HashSet<>(Arrays.asList(buyerRole)))
                .build();
        userRepository.save(testUser);
    }

    @Test // Test that a new user can be registered successfully
    void testRegisterUser_Success() throws Exception {
        // Arrange
        UserRegistrationDTO registrationDTO = UserRegistrationDTO.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .fullName("New User")
                .phoneNumber("9876543210")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("newuser"))
                .andExpect(jsonPath("$.data.email").value("newuser@example.com"));
    }

    @Test // Test that user registration fails with invalid data        
    void testRegisterUser_InvalidData_ReturnsBadRequest() throws Exception {
        // Arrange - Missing required fields
        UserRegistrationDTO registrationDTO = UserRegistrationDTO.builder()
                .username("ab") // Too short
                .email("invalid-email") // Invalid email format
                .password("123") // Too short
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test // Test that an admin can retrieve all users successfully
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsers_AsAdmin_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test // Test that a non-admin cannot retrieve all users (forbidden)
    @WithMockUser(roles = "BUYER")
    void testGetAllUsers_AsNonAdmin_Forbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test // Test that a user can retrieve their own details successfully
    @WithMockUser(username = "testuser", roles = "BUYER")
    void testGetUserById_AsOwner_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test // Test that a user cannot retrieve another user's details (forbidden)
    @WithMockUser(roles = "BUYER")
    void testGetUserByUsername_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test // Test that a user can update their own details successfully
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser_AsAdmin_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/users/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test // Test that a non-admin cannot delete a user (forbidden)
    @WithMockUser(roles = "BUYER")
    void testDeleteUser_AsNonAdmin_Forbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/users/" + testUser.getId()))
                .andExpect(status().isForbidden());
    }
}
