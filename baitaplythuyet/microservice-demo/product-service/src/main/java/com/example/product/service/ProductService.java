package com.example.product.service;



import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private int retryCounter = 0;
    private static final String SERVICE_NAME = "productService";

    /**
     * 1. RETRY Pattern
     * Tự động thử lại khi có lỗi
     * Config: maxAttempts=3, waitDuration=1s
     */
    @Retry(name = SERVICE_NAME, fallbackMethod = "getAllProductsFallback")
    public List<Product> getAllProducts() {
        retryCounter++;
        log.info("Fetching all products - attempt {}", retryCounter);

        if (retryCounter < 3) {
            log.error("Simulated temporary error");
            throw new RuntimeException("Temporary database error");
        }

        return productRepository.findAll();
    }



    /**
     * 2. CIRCUIT BREAKER Pattern
     * Ngắt mạch khi service liên tục lỗi
     * Config: failureRateThreshold=50%, waitDurationInOpenState=10s
     */
    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "getProductByIdFallback")
    public Optional<Product> getProductById(Long id) {
        log.info("Fetching product by id: {}", id);
        if (Math.random() > 0.7) {
            log.error("Random failure occurred!");
            throw new RuntimeException("Service temporarily unavailable");
        }
        return productRepository.findById(id);
    }

    /**
     * 3. RATE LIMITER Pattern
     * Giới hạn số lượng request
     * Config: limitForPeriod=10, limitRefreshPeriod=1s
     */
    @RateLimiter(name = SERVICE_NAME, fallbackMethod = "createProductFallback")
    public Product createProduct(Product product) {
        log.info("Creating product: {}", product.getName());
        return productRepository.save(product);
    }

    /**
     * 4. BULKHEAD Pattern
     * Giới hạn số lượng concurrent calls
     * Config: maxConcurrentCalls=5
     */
    @Bulkhead(name = SERVICE_NAME, fallbackMethod = "updateProductFallback")
    public Product updateProduct(Long id, Product product) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("Updating product id: {}", id);
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setDescription(product.getDescription());
                    existingProduct.setPrice(product.getPrice());
                    existingProduct.setStock(product.getStock());
                    return productRepository.save(existingProduct);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public void deleteProduct(Long id) {
        log.info("Deleting product id: {}", id);
        productRepository.deleteById(id);
    }

    // ==================== FALLBACK METHODS ====================

    private List<Product> getAllProductsFallback(Exception e) {
        log.error("Fallback: getAllProducts failed - {}", e.getMessage());
        return new ArrayList<>();
    }

    private Optional<Product> getProductByIdFallback(Long id, Exception e) {
        log.error("Fallback: getProductById {} failed - {}", id, e.getMessage());
        Product fallbackProduct = new Product();
        fallbackProduct.setId(id);
        fallbackProduct.setName("Fallback Product");
        fallbackProduct.setDescription("Service temporarily unavailable");
        fallbackProduct.setPrice(0.0);
        fallbackProduct.setStock(0);
        return Optional.of(fallbackProduct);
    }

    private Product createProductFallback(Product product, Exception e) {
        log.error("Fallback: createProduct failed - {}", e.getMessage());
        throw new RuntimeException("Rate limit exceeded. Please try again later.");
    }

    private Product updateProductFallback(Long id, Product product, Exception e) {
        log.error("Fallback: updateProduct {} failed - {}", id, e.getMessage());
        throw new RuntimeException("Service busy. Please try again later.");
    }

}
