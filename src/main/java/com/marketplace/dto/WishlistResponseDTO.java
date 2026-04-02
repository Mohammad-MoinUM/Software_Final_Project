package com.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for wishlist response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponseDTO {

    private Long id;
    private Long userId;
    @Builder.Default
    private List<ProductResponseDTO> products = new ArrayList<>();
    private Integer productCount;
}
