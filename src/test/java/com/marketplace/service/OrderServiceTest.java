package com.marketplace.service;

import com.marketplace.entity.Order;
import com.marketplace.entity.OrderStatus;
import com.marketplace.entity.Product;
import com.marketplace.entity.User;
import com.marketplace.repository.OrderRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderService
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private User testBuyer;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testBuyer = User.builder()
                .id(1L)
                .username("buyer")
                .email("buyer@example.com")
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .stockQuantity(10)
                .available(true)
                .build();

        testOrder = Order.builder()
                .id(1L)
                .orderNumber("ORD-001")
                .buyer(testBuyer)
                .products(Set.of(testProduct))
                .totalAmount(new BigDecimal("99.99"))
                .status(OrderStatus.PENDING)
                .build();
    }

    @Test
    void testFindById_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        Optional<Order> result = orderService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("ORD-001", result.get().getOrderNumber());
    }

    @Test
    void testFindAll_Success() {
        // Arrange
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findAll()).thenReturn(orders);

        // Act
        List<Order> result = orderService.findAll();

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void testFindByBuyerId_Success() {
        // Arrange
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByBuyerId(1L)).thenReturn(orders);

        // Act
        List<Order> result = orderService.findByBuyerId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("buyer", result.get(0).getBuyer().getUsername());
    }

    @Test
    void testFindByStatus_Success() {
        // Arrange
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(orders);

        // Act
        List<Order> result = orderService.findByStatus(OrderStatus.PENDING);

        // Assert
        assertEquals(1, result.size());
        assertEquals(OrderStatus.PENDING, result.get(0).getStatus());
    }

    @Test
    void testCreateOrder_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testBuyer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.createOrder(1L, Set.of(1L));

        // Assert
        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(productService, times(1)).decreaseStock(1L, 1);
    }

    @Test
    void testCreateOrder_BuyerNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(999L, Set.of(1L));
        });
    }

    @Test
    void testCreateOrder_ProductNotAvailable_ThrowsException() {
        // Arrange
        testProduct.setAvailable(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testBuyer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            orderService.createOrder(1L, Set.of(1L));
        });
    }

    @Test
    void testUpdateOrderStatus_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.updateOrderStatus(1L, OrderStatus.SHIPPED);

        // Assert
        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCancelOrder_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        Order result = orderService.cancelOrder(1L);

        // Assert
        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCancelOrder_AlreadyDelivered_ThrowsException() {
        // Arrange
        testOrder.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            orderService.cancelOrder(1L);
        });
    }

    @Test
    void testDeleteOrder_Success() {
        // Arrange
        when(orderRepository.existsById(1L)).thenReturn(true);

        // Act
        orderService.deleteOrder(1L);

        // Assert
        verify(orderRepository, times(1)).deleteById(1L);
    }
}
