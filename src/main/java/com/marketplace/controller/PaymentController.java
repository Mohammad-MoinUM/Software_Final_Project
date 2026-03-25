package com.marketplace.controller;

import com.marketplace.dto.*;
import com.marketplace.entity.Payment;
import com.marketplace.entity.PaymentStatus;
import com.marketplace.service.PaymentService;
import com.marketplace.util.EntityMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Payment operations
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final EntityMapper entityMapper;

    /**
     * Create a payment
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> createPayment(
            @Valid @RequestBody PaymentCreateDTO createDTO) {
        
        log.info("Creating payment for order {}", createDTO.getOrderId());
        
        Payment payment = entityMapper.toPaymentEntity(createDTO);
        Payment createdPayment = paymentService.createPayment(payment);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment created successfully", 
                        entityMapper.toPaymentResponseDTO(createdPayment)));
    }

    /**
     * Process payment
     */
    @PostMapping("/{id}/process")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> processPayment(@PathVariable Long id) {
        log.info("Processing payment {}", id);
        
        Payment payment = paymentService.processPayment(id);
        
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", 
                entityMapper.toPaymentResponseDTO(payment)));
    }

    /**
     * Get payment by order ID
     */
    @GetMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> getPaymentByOrderId(@PathVariable Long orderId) {
        log.info("Fetching payment for order {}", orderId);
        
        Payment payment = paymentService.getPaymentByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for order"));
        
        return ResponseEntity.ok(ApiResponse.success("Payment retrieved successfully", 
                entityMapper.toPaymentResponseDTO(payment)));
    }

    /**
     * Update payment status (ADMIN only)
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus status) {
        
        log.info("Updating payment {} status to {}", id, status);
        
        Payment payment = paymentService.updatePaymentStatus(id, status);
        
        return ResponseEntity.ok(ApiResponse.success("Payment status updated", 
                entityMapper.toPaymentResponseDTO(payment)));
    }
}
