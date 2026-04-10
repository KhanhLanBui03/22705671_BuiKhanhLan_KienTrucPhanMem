package com.example.app.order.service;

import com.example.app.order.dto.OrderDto;
import com.example.app.order.entity.Order;
import com.example.app.order.repository.OrderRepository;
import com.example.app.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;  // gọi trực tiếp trong cùng JVM

    @Transactional
    public OrderDto.Response createOrder(OrderDto.CreateRequest request) {
        log.info("Tạo order cho: {}", request.getCustomerEmail());

        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setTotalAmount(request.getTotalAmount());

        Order saved = orderRepository.save(order);

        // Gọi PaymentService trực tiếp (cùng JVM, không qua HTTP)
        paymentService.initializePayment(saved.getId(), saved.getTotalAmount());

        return toResponse(saved);
    }

    @Transactional
    public OrderDto.Response confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order không tồn tại: " + orderId));

        order.setStatus(Order.OrderStatus.CONFIRMED);
        return toResponse(orderRepository.save(order));
    }

    public List<OrderDto.Response> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OrderDto.Response getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy order: " + id));
    }

    private OrderDto.Response toResponse(Order order) {
        OrderDto.Response res = new OrderDto.Response();
        res.setId(order.getId());
        res.setCustomerName(order.getCustomerName());
        res.setCustomerEmail(order.getCustomerEmail());
        res.setTotalAmount(order.getTotalAmount());
        res.setStatus(order.getStatus().name());
        res.setCreatedAt(order.getCreatedAt().toString());
        return res;
    }
}
