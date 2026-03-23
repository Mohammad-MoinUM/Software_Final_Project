package com.marketplace.controller;

import com.marketplace.dto.*;
import com.marketplace.entity.RoleType;
import com.marketplace.entity.User;
import com.marketplace.service.UserService;
import com.marketplace.util.EntityMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST Controller for User operations
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final EntityMapper entityMapper;

    /**
     * Get all users (ADMIN only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {
        log.info("Fetching all users");
        
        List<UserResponseDTO> users = userService.findAll().stream()
                .map(entityMapper::toUserResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwner(authentication, #id)")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {
        log.info("Fetching user with id: {}", id);
        
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        return ResponseEntity.ok(ApiResponse.success(entityMapper.toUserResponseDTO(user)));
    }

    /**
     * Get user by username
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserByUsername(@PathVariable String username) {
        log.info("Fetching user with username: {}", username);
        
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
        
        return ResponseEntity.ok(ApiResponse.success(entityMapper.toUserResponseDTO(user)));
    }

    /**
     * Register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> registerUser(
            @Valid @RequestBody UserRegistrationDTO registrationDTO) {
        
        log.info("Registering new user: {}", registrationDTO.getUsername());
        
        User user = entityMapper.toUserEntity(registrationDTO);
        
        // Default role is BUYER if not specified
        Set<RoleType> roles = registrationDTO.getRoles() != null && !registrationDTO.getRoles().isEmpty()
                ? registrationDTO.getRoles().stream()
                    .map(RoleType::valueOf)
                    .collect(Collectors.toSet())
                : Set.of(RoleType.BUYER);
        
        User createdUser = userService.createUser(user, roles);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", 
                        entityMapper.toUserResponseDTO(createdUser)));
    }

    /**
     * Create user with specific roles (ADMIN only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(
            @Valid @RequestBody UserRegistrationDTO registrationDTO) {
        
        log.info("Admin creating new user: {}", registrationDTO.getUsername());
        
        User user = entityMapper.toUserEntity(registrationDTO);
        
        Set<RoleType> roles = registrationDTO.getRoles() != null && !registrationDTO.getRoles().isEmpty()
                ? registrationDTO.getRoles().stream()
                    .map(RoleType::valueOf)
                    .collect(Collectors.toSet())
                : Set.of(RoleType.BUYER);
        
        User createdUser = userService.createUser(user, roles);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", 
                        entityMapper.toUserResponseDTO(createdUser)));
    }

    /**
     * Update user profile
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwner(authentication, #id)")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        
        log.info("Updating user with id: {}", id);
        
        User userUpdate = entityMapper.toUserEntity(updateDTO);
        User updatedUser = userService.updateUser(id, userUpdate);
        
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", 
                entityMapper.toUserResponseDTO(updatedUser)));
    }

    /**
     * Delete user (ADMIN only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with id: {}", id);
        
        userService.deleteUser(id);
        
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    /**
     * Toggle user status (ADMIN only)
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> toggleUserStatus(@PathVariable Long id) {
        log.info("Toggling status for user id: {}", id);
        
        userService.toggleUserStatus(id);
        
        return ResponseEntity.ok(ApiResponse.success("User status toggled successfully", null));
    }

    /**
     * Get users by role (ADMIN only)
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getUsersByRole(@PathVariable String role) {
        log.info("Fetching users with role: {}", role);
        
        RoleType roleType = RoleType.valueOf(role.toUpperCase());
        List<UserResponseDTO> users = userService.findByRole(roleType).stream()
                .map(entityMapper::toUserResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }
}
