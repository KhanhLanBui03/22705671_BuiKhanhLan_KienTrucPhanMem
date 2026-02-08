package com.example.product.config;



import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            log.info("Initializing sample products...");

            productRepository.saveAll(Arrays.asList(
                    new Product(null, "iPhone 15 Pro", "Latest iPhone", 999.99, 50),
                    new Product(null, "MacBook Pro M3", "14-inch laptop", 1999.99, 30),
                    new Product(null, "iPad Air", "10.9-inch tablet", 599.99, 100),
                    new Product(null, "Apple Watch", "Series 9", 429.99, 75),
                    new Product(null, "AirPods Pro", "Noise cancellation", 249.99, 200)
            ));
            log.info("Sample products created!");
        }
    }
}
