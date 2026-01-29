package com.example.bai3.controller;




import com.example.bai3.dto.CreateOrderRequest;
import com.example.bai3.entity.Order;
import com.example.bai3.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    /**
     * POST /api/orders
     * Tạo đơn hàng KHÔNG dùng Message Queue
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("");
        log.info("╔═══════════════════════════════════════════════════════════════╗");
        log.info("║  📞 API ĐƯỢC GỌI: POST /api/orders                           ║");
        log.info("╚═══════════════════════════════════════════════════════════════╝");
        log.info("📦 Request: {}", request);
        log.info("");

        long apiStartTime = System.currentTimeMillis();

        try {
            Order order = orderService.createOrderWithoutMQ(
                    request.getCustomerEmail(),
                    request.getProductName(),
                    request.getAmount()
            );

            long apiDuration = System.currentTimeMillis() - apiStartTime;

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đơn hàng đã được tạo và email đã gửi");
            response.put("data", Map.of(
                    "orderId", order.getId(),
                    "status", order.getStatus(),
                    "customerEmail", order.getCustomerEmail(),
                    "productName", order.getProductName(),
                    "amount", order.getAmount(),
                    "emailSent", order.getEmailSent(),
                    "createdAt", order.getCreatedAt()
            ));
            response.put("performance", Map.of(
                    "apiResponseTime", apiDuration + " ms",
                    "processingTime", order.getProcessingTimeMs() + " ms",
                    "warning", "⚠️ API mất " + apiDuration + " ms vì phải đợi gửi email!"
            ));

            log.info("╔═══════════════════════════════════════════════════════════════╗");
            log.info("║  🎉 API TRẢ VỀ RESPONSE (THÀNH CÔNG)                         ║");
            log.info("╚═══════════════════════════════════════════════════════════════╝");
            log.info("⏱️  API response time: {} ms", apiDuration);
            log.info("");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            long apiDuration = System.currentTimeMillis() - apiStartTime;

            log.error("╔═══════════════════════════════════════════════════════════════╗");
            log.error("║  💥 API TRẢ VỀ LỖI                                            ║");
            log.error("╚═══════════════════════════════════════════════════════════════╝");
            log.error("⏱️  API response time: {} ms", apiDuration);
            log.error("❌ Error: {}", e.getMessage());
            log.error("");

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Tạo đơn hàng thất bại");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("performance", Map.of(
                    "apiResponseTime", apiDuration + " ms",
                    "problem", "❌ Email fail → Đơn hàng bị huỷ → User không đặt được hàng!"
            ));

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    /**
     * GET /api/orders
     * Lấy danh sách tất cả đơn hàng
     */
    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", orders.size());
        response.put("data", orders);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/orders/{id}
     * Lấy đơn hàng theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", order);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(errorResponse);
        }
    }

    /**
     * GET /api/orders/by-email/{email}
     * Lấy đơn hàng theo email
     */
    @GetMapping("/by-email/{email}")
    public ResponseEntity<?> getOrdersByEmail(@PathVariable String email) {
        List<Order> orders = orderService.getOrdersByEmail(email);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", orders.size());
        response.put("data", orders);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/orders/statistics
     * Lấy thống kê
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        OrderService.OrderStatistics stats = orderService.getStatistics();

        double successRate = stats.totalOrders() > 0
                ? (stats.successOrders() * 100.0 / stats.totalOrders())
                : 0;

        double failureRate = stats.totalOrders() > 0
                ? (stats.failedOrders() * 100.0 / stats.totalOrders())
                : 0;

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of(
                "totalOrders", stats.totalOrders(),
                "successOrders", stats.successOrders(),
                "failedOrders", stats.failedOrders(),
                "pendingOrders", stats.pendingOrders(),
                "successRate", String.format("%.1f%%", successRate),
                "failureRate", String.format("%.1f%%", failureRate),
                "averageProcessingTime", stats.averageProcessingTimeMs() != null
                        ? String.format("%.0f ms", stats.averageProcessingTimeMs())
                        : "N/A"
        ));
        response.put("analysis", Map.of(
                "problems", List.of(
                        "❌ Thời gian xử lý trung bình: " +
                                (stats.averageProcessingTimeMs() != null ? String.format("%.0f ms", stats.averageProcessingTimeMs()) : "N/A"),
                        "❌ Tỷ lệ thất bại: " + String.format("%.1f%%", failureRate) + " (email fail → đơn bị huỷ)",
                        "❌ User phải chờ lâu (3-5 giây)",
                        "❌ Không có cơ chế retry"
                ),
                "solution", "Sử dụng Message Queue (RabbitMQ/Kafka) để xử lý email bất đồng bộ"
        ));

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/orders/info
     * Thông tin về demo
     */
    @GetMapping("/info")
    public ResponseEntity<?> getInfo() {
        Map<String, Object> info = new HashMap<>();

        info.put("title", "Demo: Vấn đề khi KHÔNG dùng Message Queue");
        info.put("version", "1.0 - MySQL Edition");

        info.put("scenario", Map.of(
                "description", "User đặt hàng, hệ thống cần gửi email xác nhận",
                "emailDelay", "3-5 giây",
                "emailFailRate", "30%",
                "database", "MySQL"
        ));

        info.put("problems", List.of(
                "❌ API response chậm (3-5 giây) vì phải đợi gửi email",
                "❌ Dễ bị timeout nếu email service chậm",
                "❌ Email fail → Transaction rollback → Đơn hàng bị huỷ",
                "❌ Email service down → Không thể đặt hàng",
                "❌ User experience tệ (phải đợi lâu)",
                "❌ Không có cơ chế retry"
        ));

        info.put("endpoints", Map.of(
                "createOrder", "POST /api/orders",
                "getAllOrders", "GET /api/orders",
                "getOrderById", "GET /api/orders/{id}",
                "getOrdersByEmail", "GET /api/orders/by-email/{email}",
                "getStatistics", "GET /api/orders/statistics",
                "getInfo", "GET /api/orders/info"
        ));

        info.put("testInstructions", List.of(
                "1. Import Postman collection: order-without-mq.postman_collection.json",
                "2. Gửi request 'Create Order' nhiều lần",
                "3. Quan sát logs và thời gian response",
                "4. Xem statistics để thấy tỷ lệ thất bại",
                "5. Khoảng 70% request thành công (nhưng chậm 3-5s)",
                "6. Khoảng 30% request thất bại (email lỗi → đơn bị huỷ)"
        ));

        info.put("solution", Map.of(
                "approach", "Sử dụng Message Queue (RabbitMQ, Kafka)",
                "benefits", List.of(
                        "✅ API response < 100ms",
                        "✅ Email fail không ảnh hưởng đơn hàng",
                        "✅ Có cơ chế retry tự động",
                        "✅ Dễ dàng scale workers",
                        "✅ Hệ thống ổn định hơn"
                )
        ));

        return ResponseEntity.ok(info);
    }
}