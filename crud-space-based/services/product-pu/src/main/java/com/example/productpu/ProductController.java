package com.example.productpu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // Service Ghi: Chống chỉ định dùng GET ở đây theo sơ đồ tách rời
    
    @PostMapping
    public Product create(@RequestBody Product product) {
        redisTemplate.opsForValue().set("PRODUCT:" + product.getId(), product);
        sendToKafka("CREATE", product);
        return product;
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable String id, @RequestBody Product product) {
        product.setId(id);
        redisTemplate.opsForValue().set("PRODUCT:" + id, product);
        sendToKafka("UPDATE", product);
        return product;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        Product product = new Product();
        product.setId(id);
        redisTemplate.delete("PRODUCT:" + id);
        sendToKafka("DELETE", product);
    }

    private void sendToKafka(String action, Product product) {
        try {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("action", action);
            node.set("product", objectMapper.valueToTree(product));
            kafkaTemplate.send("product-topic", node.toString());
            System.out.println("Sent to Kafka: " + action + " - " + product.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
