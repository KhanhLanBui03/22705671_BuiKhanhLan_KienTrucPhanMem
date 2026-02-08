package com.example.order.client;


import com.example.order.dto.ProductDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductClientFallback implements ProductClient {

    @Override
    public ProductDTO getProductById(Long id) {
        log.error("Product Service unavailable. Using fallback for product id: {}", id);

        ProductDTO fallback = new ProductDTO();
        fallback.setId(id);
        fallback.setName("Product Unavailable");
        fallback.setDescription("Product service is temporarily unavailable");
        fallback.setPrice(0.0);
        fallback.setStock(0);

        return fallback;
    }
}
