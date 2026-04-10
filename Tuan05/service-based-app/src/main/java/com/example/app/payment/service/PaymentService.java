package com.example.app.payment.service;

import com.example.app.payment.entity.Payment;
import com.example.app.payment.repository.PaymentRepository;
import com.example.app.shipping.service.ShippingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ShippingService shippingService;

    /**
     * Được gọi bởi OrderService khi tạo order mới
     */
    @Transactional
    public Payment initializePayment(Long orderId, BigDecimal amount) {
        log.info("Khởi tạo payment cho orderId={}, amount={}", orderId, amount);

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);

        return paymentRepository.save(payment);
    }

    /**
     * Xác nhận thanh toán thành công → trigger tạo shipment
     */
    @Transactional
    public Payment completePayment(Long paymentId, Payment.PaymentMethod method) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment không tồn tại: " + paymentId));

        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setMethod(method);
        payment.setPaidAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        // Sau khi thanh toán xong → gọi ShippingService để tạo vận đơn
        shippingService.createShipment(payment.getOrderId());

        return saved;
    }

    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy payment cho order: " + orderId));
    }
}
