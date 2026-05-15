package com.example.productpersistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @Autowired
    private ProductRepository productRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "product-topic", groupId = "product-group")
    public void consume(String message) {
        try {
            System.out.println("Consumed message: " + message);
            JsonNode node = objectMapper.readTree(message);
            String action = node.get("action").asText();

            if ("CREATE".equals(action) || "UPDATE".equals(action) || "SAVE".equals(action)) {
                Product product = objectMapper.treeToValue(node.get("product"), Product.class);
                productRepository.save(product);
                System.out.println("Product saved/updated in MySQL: " + product.getId());
            } else if ("DELETE".equals(action)) {
                // Thử lấy product từ node "product" trước, nếu không có thì lấy "id" trực tiếp
                String id;
                if (node.has("product")) {
                    id = node.get("product").get("id").asText();
                } else {
                    id = node.get("id").asText();
                }
                productRepository.deleteById(id);
                System.out.println("Product deleted from MySQL: " + id);
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
