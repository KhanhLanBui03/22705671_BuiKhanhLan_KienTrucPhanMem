package com.example.app.payment.controller;

import com.example.app.payment.entity.Payment;
import com.example.app.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    @PutMapping("/{paymentId}/complete")
    public ResponseEntity<Payment> completePayment(
            @PathVariable Long paymentId,
            @RequestParam Payment.PaymentMethod method) {
        return ResponseEntity.ok(paymentService.completePayment(paymentId, method));
    }
}
