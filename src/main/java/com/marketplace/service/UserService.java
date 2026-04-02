package com.marketplace.service;

import com.marketplace.entity.Role;
import com.marketplace.entity.RoleType;
import com.marketplace.entity.User;
import com.marketplace.repository.RoleRepository;
import com.marketplace.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service layer for User operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Find user by ID
     */
    public Optional<User> findById(Long id) {
        log.debug("Finding user by id: {}", id);
        return userRepository.findById(id);
    }

    /**
     * Find user by username
     */
    public Optional<User> findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    /**
     * Get all users
     */
    public List<User> findAll() {
        log.debug("Fetching all users");
        return userRepository.findAll();
    }

    /**
     * Create a new user with specified roles
     */
    public User createUser(User user, Set<RoleType> roleTypes) {
        log.info("Creating new user: {}", user.getUsername());
        
        // Check if username or email already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign roles
        for (RoleType roleType : roleTypes) {
            Role role = roleRepository.findByName(roleType)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleType));
            user.addRole(role);
        }

        return userRepository.save(user);
    }

    /**
     * Update existing user
     */
    public User updateUser(Long id, User updatedUser) {
        log.info("Updating user with id: {}", id);
        
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        // Update fields
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.findByEmail(updatedUser.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists: " + updatedUser.getEmail());
            }
            existingUser.setEmail(updatedUser.getEmail());
        }
        
        if (updatedUser.getFullName() != null) {
            existingUser.setFullName(updatedUser.getFullName());
        }
        
        if (updatedUser.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        }
        
        if (updatedUser.getEnabled() != null) {
            existingUser.setEnabled(updatedUser.getEnabled());
        }

        return userRepository.save(existingUser);
    }

    /**
     * Change user password
     */
    public void changePassword(Long id, String newPassword) {
        log.info("Changing password for user id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Delete user by ID
     */
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        
        userRepository.deleteById(id);
    }

    /**
     * Enable or disable user
     */
    public void toggleUserStatus(Long id) {
        log.info("Toggling status for user id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        user.setEnabled(!user.getEnabled());
        userRepository.save(user);
    }

    /**
     * Get all users by role
     */
    public List<User> findByRole(RoleType roleType) {
        log.debug("Finding users by role: {}", roleType);
        return userRepository.findByRolesName(roleType);
    }
}
