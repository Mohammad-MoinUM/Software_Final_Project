package com.marketplace.security;

import com.marketplace.entity.User;
import com.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Security helper for user ownership checks
 */
@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

    private final UserRepository userRepository;

    /**
     * Check if the authenticated user is the owner of the resource
     */
    public boolean isOwner(Authentication authentication, Long userId) {
        if (authentication == null || userId == null) {
            return false;
        }

        String username = authentication.getName();
        User user = userRepository.findById(userId).orElse(null);
        
        return user != null && user.getUsername().equals(username);
    }
}
