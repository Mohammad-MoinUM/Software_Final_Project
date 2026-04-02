package com.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for cart item response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDTO {

    private Long id;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private Boolean available;
    private Integer stockQuantity;
}
