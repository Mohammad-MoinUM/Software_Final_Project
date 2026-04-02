package com.marketplace.util;

import com.marketplace.dto.*;
import com.marketplace.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
 * Utility class for mapping between entities and DTOs
 */
@Component
public class EntityMapper {

    /**
     * Convert User entity to UserResponseDTO
     */
    public UserResponseDTO toUserResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .enabled(user.getEnabled())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Convert Product entity to ProductResponseDTO
     */
    public ProductResponseDTO toProductResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .imageUrl(product.getImageUrl())
                .available(product.getAvailable())
                .sellerId(product.getSeller() != null ? product.getSeller().getId() : null)
                .sellerUsername(product.getSeller() != null ? product.getSeller().getUsername() : null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    /**
     * Convert Order entity to OrderResponseDTO
     */
    public OrderResponseDTO toOrderResponseDTO(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .buyerId(order.getBuyer() != null ? order.getBuyer().getId() : null)
                .buyerUsername(order.getBuyer() != null ? order.getBuyer().getUsername() : null)
                .products(order.getProducts().stream()
                        .map(this::toProductResponseDTO)
                        .collect(Collectors.toSet()))
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    /**
     * Convert UserRegistrationDTO to User entity
     */
    public User toUserEntity(UserRegistrationDTO dto) {
        return User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .fullName(dto.getFullName())
                .phoneNumber(dto.getPhoneNumber())
                .enabled(true)
                .build();
    }

    /**
     * Convert UserUpdateDTO to User entity (for updating)
     */
    public User toUserEntity(UserUpdateDTO dto) {
        return User.builder()
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .phoneNumber(dto.getPhoneNumber())
                .enabled(dto.getEnabled())
                .build();
    }

    /**
     * Convert ProductCreateDTO to Product entity
     */
    public Product toProductEntity(ProductCreateDTO dto) {
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .category(dto.getCategory())
                .imageUrl(dto.getImageUrl())
                .available(dto.getAvailable() != null ? dto.getAvailable() : true)
                .build();
    }

    /**
     * Convert ProductUpdateDTO to Product entity (for updating)
     */
    public Product toProductEntity(ProductUpdateDTO dto) {
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .category(dto.getCategory())
                .imageUrl(dto.getImageUrl())
                .available(dto.getAvailable())
                .build();
    }

    /**
     * Convert Review entity to ReviewResponseDTO
     */
    public ReviewResponseDTO toReviewResponseDTO(Review review) {
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .productId(review.getProduct() != null ? review.getProduct().getId() : null)
                .productName(review.getProduct() != null ? review.getProduct().getName() : null)
                .userId(review.getUser() != null ? review.getUser().getId() : null)
                .username(review.getUser() != null ? review.getUser().getUsername() : null)
                .userFullName(review.getUser() != null ? review.getUser().getFullName() : null)
                .rating(review.getRating())
                .comment(review.getComment())
                .verifiedPurchase(review.getVerifiedPurchase())
                .createdAt(review.getCreatedAt())
                .build();
    }

    /**
     * Convert ReviewCreateDTO to Review entity
     */
    public Review toReviewEntity(ReviewCreateDTO dto) {
        return Review.builder()
                .product(Product.builder().id(dto.getProductId()).build())
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();
    }

    /**
     * Convert CartItem entity to CartItemResponseDTO
     */
    public CartItemResponseDTO toCartItemResponseDTO(CartItem cartItem) {
        Product product = cartItem.getProduct();
        BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        
        return CartItemResponseDTO.builder()
                .id(cartItem.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImageUrl(product.getImageUrl())
                .productPrice(product.getPrice())
                .quantity(cartItem.getQuantity())
                .subtotal(subtotal)
                .available(product.getAvailable())
                .stockQuantity(product.getStockQuantity())
                .build();
    }

    /**
     * Convert Wishlist entity to WishlistResponseDTO
     */
    public WishlistResponseDTO toWishlistResponseDTO(Wishlist wishlist) {
        return WishlistResponseDTO.builder()
                .id(wishlist.getId())
                .userId(wishlist.getUser() != null ? wishlist.getUser().getId() : null)
                .products(wishlist.getProducts().stream()
                        .map(this::toProductResponseDTO)
                        .collect(Collectors.toList()))
                .productCount(wishlist.getProducts().size())
                .build();
    }

    /**
     * Convert Payment entity to PaymentResponseDTO
     */
    public PaymentResponseDTO toPaymentResponseDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrder() != null ? payment.getOrder().getId() : null)
                .orderNumber(payment.getOrder() != null ? payment.getOrder().getOrderNumber() : null)
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    /**
     * Convert PaymentCreateDTO to Payment entity
     */
    public Payment toPaymentEntity(PaymentCreateDTO dto) {
        return Payment.builder()
                .order(Order.builder().id(dto.getOrderId()).build())
                .paymentMethod(dto.getPaymentMethod())
                .amount(dto.getAmount())
                .paymentDetails(dto.getPaymentDetails())
                .build();
    }
}
