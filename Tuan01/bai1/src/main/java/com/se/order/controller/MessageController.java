package com.se.order.controller;
import com.se.order.producer.MessageProducer;
import com.se.order.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {

    private final MessageProducer producer;
    private final MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendMessage(
            @RequestBody Map<String, String> request) {

        String message = request.get("message");
        producer.push(message);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Sent to queue"
        ));
    }

    @GetMapping("/get")
    public ResponseEntity<Map<String, String>> getMessage() {
        String message = messageService.getMessage();
        
        if (message != null) {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", message
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "status", "empty",
                    "message", "No message in queue"
            ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Exercise 1 is running!");
    }
}