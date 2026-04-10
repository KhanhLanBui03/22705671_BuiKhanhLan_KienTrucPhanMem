package com.example.app.shipping.service;

import com.example.app.shipping.entity.Shipment;
import com.example.app.shipping.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingService {

    private final ShipmentRepository shipmentRepository;

    /**
     * Được gọi bởi PaymentService sau khi thanh toán hoàn tất
     */
    @Transactional
    public Shipment createShipment(Long orderId) {
        log.info("Tạo shipment cho orderId={}", orderId);

        Shipment shipment = new Shipment();
        shipment.setOrderId(orderId);
        shipment.setTrackingCode("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        return shipmentRepository.save(shipment);
    }

    @Transactional
    public Shipment updateStatus(Long shipmentId, Shipment.ShipmentStatus newStatus) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Shipment không tồn tại: " + shipmentId));

        shipment.setStatus(newStatus);

        if (newStatus == Shipment.ShipmentStatus.SHIPPED) {
            shipment.setShippedAt(LocalDateTime.now());
        } else if (newStatus == Shipment.ShipmentStatus.DELIVERED) {
            shipment.setDeliveredAt(LocalDateTime.now());
        }

        return shipmentRepository.save(shipment);
    }

    public Shipment trackByCode(String trackingCode) {
        return shipmentRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Mã tracking không hợp lệ: " + trackingCode));
    }

    public Shipment getShipmentByOrderId(Long orderId) {
        return shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Chưa có shipment cho order: " + orderId));
    }
}
