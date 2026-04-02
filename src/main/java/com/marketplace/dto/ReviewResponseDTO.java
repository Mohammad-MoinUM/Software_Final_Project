package com.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for review response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {

    private Long id;
    private Long productId;
    private String productName;
    private Long userId;
    private String username;
    private String userFullName;
    private Integer rating;
    private String comment;
    private Boolean verifiedPurchase;
    private LocalDateTime createdAt;
}
