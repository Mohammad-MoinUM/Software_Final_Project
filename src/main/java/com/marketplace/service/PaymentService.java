package com.marketplace.service;

import com.marketplace.entity.Payment;
import com.marketplace.entity.PaymentStatus;
import com.marketplace.entity.Order;
import com.marketplace.repository.PaymentRepository;
import com.marketplace.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Service class for Payment operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final EmailService emailService;

    /**
     * Create a payment
     */
    public Payment createPayment(Payment payment) {
        log.info("Creating payment for order {}", payment.getOrder().getId());

        Order order = orderRepository.findById(payment.getOrder().getId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Check if payment already exists for order
        if (paymentRepository.existsByOrderId(order.getId())) {
            throw new IllegalStateException("Payment already exists for this order");
        }

        payment.setOrder(order);
        payment.setStatus(PaymentStatus.PENDING);
        
        // Generate transaction ID
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        Payment savedPayment = paymentRepository.save(payment);

        // Send email notification
        try {
            emailService.sendPaymentConfirmationEmail(order.getBuyer().getEmail(), 
                order.getOrderNumber(), savedPayment.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to send payment confirmation email", e);
        }

        return savedPayment;
    }

    /**
     * Update payment status
     */
    public Payment updatePaymentStatus(Long paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    /**
     * Process payment (simulate payment processing)
     */
    public Payment processPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        log.info("Processing payment {} with method {}", paymentId, payment.getPaymentMethod());

        // Simulate payment processing
        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);

        // Simulate successful payment
        payment.setStatus(PaymentStatus.COMPLETED);
        Payment completedPayment = paymentRepository.save(payment);

        // Send confirmation email
        try {
            emailService.sendPaymentSuccessEmail(
                payment.getOrder().getBuyer().getEmail(),
                payment.getOrder().getOrderNumber(),
                payment.getAmount().toString()
            );
        } catch (Exception e) {
            log.error("Failed to send payment success email", e);
        }

        return completedPayment;
    }

    /**
     * Get payment by order ID
     */
    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    /**
     * Get payment by ID
     */
    @Transactional(readOnly = true)
    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }
}
