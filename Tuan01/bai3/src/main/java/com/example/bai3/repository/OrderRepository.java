package com.example.bai3.repository;

import com.example.bai3.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.bai3.entity.Order.OrderStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByCustomerEmail(String email);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(OrderStatus status);

    @Query("SELECT AVG(o.processingTimeMs) FROM Order o WHERE o.processingTimeMs IS NOT NULL")
    Double getAverageProcessingTime();
}