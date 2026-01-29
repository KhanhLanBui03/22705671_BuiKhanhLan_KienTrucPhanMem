package com.example.bai3.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String customerEmail;

    @Column(nullable = false, length = 200)
    private String productName;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column
    private Long processingTimeMs; // Thời gian xử lý (ms)

    @Column
    private Boolean emailSent = false;

    public enum OrderStatus {
        PENDING,    // Đang xử lý
        SUCCESS,    // Thành công
        FAILED      // Thất bại
    }

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = OrderStatus.PENDING;
        }
        if (this.emailSent == null) {
            this.emailSent = false;
        }
    }
}