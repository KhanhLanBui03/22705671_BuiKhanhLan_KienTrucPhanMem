package com.example.order.service;
import com.example.order.client.ProductClient;
import com.example.order.dto.ProductDTO;
import com.example.order.entity.Order;
import com.example.order.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    private static final String SERVICE_NAME = "orderService";

    /**
     * Tạo order - gọi Product Service với Resilience4j
     * Kết hợp: Retry + Circuit Breaker + Rate Limiter
     */
    @Retry(name = SERVICE_NAME, fallbackMethod = "createOrderFallback")
    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "createOrderFallback")
    @RateLimiter(name = SERVICE_NAME, fallbackMethod = "createOrderFallback")
    public Order createOrder(Long productId, Integer quantity) {
        log.info("Creating order for product {} with quantity {}", productId, quantity);
        // Gọi Product Service qua Feign
        ProductDTO product = productClient.getProductById(productId);
        // Validate product
        if (product.getStock() < quantity) {
            throw new RuntimeException("Not enough stock. Available: " + product.getStock());
        }
        // Tạo order
        Order order = new Order();
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalPrice(product.getPrice() * quantity);
        order.setStatus("CREATED");
        order.setOrderDate(LocalDateTime.now());
        order.setProductName(product.getName());
        order.setProductPrice(product.getPrice());

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        log.info("Fetching all orders");
        List<Order> orders = orderRepository.findAll();

        // Enrich với product info
        orders.forEach(order -> {
            try {
                ProductDTO product = productClient.getProductById(order.getProductId());
                order.setProductName(product.getName());
                order.setProductPrice(product.getPrice());
            } catch (Exception e) {
                log.error("Failed to fetch product info for order {}", order.getId());
                order.setProductName("Unknown");
                order.setProductPrice(0.0);
            }
        });

        return orders;
    }

    public Optional<Order> getOrderById(Long id) {
        log.info("Fetching order id: {}", id);
        return orderRepository.findById(id);
    }

    public void deleteOrder(Long id) {
        log.info("Deleting order id: {}", id);
        orderRepository.deleteById(id);
    }

    // FALLBACK
    private Order createOrderFallback(Long productId, Integer quantity, Exception e) {
        log.error("Fallback triggered for createOrder: {}", e.getMessage());
        throw new RuntimeException("Unable to create order. Please try again later.");
    }
}
