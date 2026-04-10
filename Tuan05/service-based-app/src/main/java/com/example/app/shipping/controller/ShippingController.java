package com.example.app.shipping.controller;

import com.example.app.shipping.entity.Shipment;
import com.example.app.shipping.service.ShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingService shippingService;

    @GetMapping("/track/{trackingCode}")
    public ResponseEntity<Shipment> track(@PathVariable String trackingCode) {
        return ResponseEntity.ok(shippingService.trackByCode(trackingCode));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Shipment> getByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(shippingService.getShipmentByOrderId(orderId));
    }

    @PutMapping("/{shipmentId}/status")
    public ResponseEntity<Shipment> updateStatus(
            @PathVariable Long shipmentId,
            @RequestParam Shipment.ShipmentStatus status) {
        return ResponseEntity.ok(shippingService.updateStatus(shipmentId, status));
    }
}
