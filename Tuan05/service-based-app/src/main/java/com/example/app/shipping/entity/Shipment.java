package com.example.app.shipping.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Data
@NoArgsConstructor
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private String trackingCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    private String deliveryAddress;

    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (status == null) status = ShipmentStatus.PREPARING;
    }

    public enum ShipmentStatus {
        PREPARING, SHIPPED, IN_TRANSIT, DELIVERED, FAILED
    }
}
