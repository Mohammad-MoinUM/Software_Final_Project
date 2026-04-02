package com.marketplace.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO for creating an order
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDTO {

    // Optional: when provided, admin can create orders on behalf of buyers
    // Otherwise the authenticated user's ID is used
    private Long buyerId;

    @NotEmpty(message = "At least one product must be ordered")
    private Set<Long> productIds;
}
