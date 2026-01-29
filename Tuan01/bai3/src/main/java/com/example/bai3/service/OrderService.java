package com.example.bai3.service;



import com.example.bai3.repository.OrderRepository;
import com.example.bai3.entity.Order;
import com.example.bai3.entity.Order.OrderStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final EmailService emailService;

    /**
     * ❌ CÁCH LÀM SAI: Không dùng Message Queue
     *
     * VẤN ĐỀ NGHIÊM TRỌNG:
     * 1. API phải chờ email gửi xong (3-5 giây) → User chờ lâu
     * 2. Email fail → Transaction rollback → Đơn hàng bị huỷ
     * 3. Email service down → Không thể đặt hàng
     * 4. Không có cơ chế retry
     */
    @Transactional
    public Order createOrderWithoutMQ(String customerEmail, String productName, Double amount) {
        long startTime = System.currentTimeMillis();

        log.info("");
        log.info("╔═══════════════════════════════════════════════════════════════╗");
        log.info("║  BẮT ĐẦU XỬ LÝ ĐƠN HÀNG (KHÔNG DÙNG MESSAGE QUEUE)          ║");
        log.info("╚═══════════════════════════════════════════════════════════════╝");
        log.info("📦 Sản phẩm: {}", productName);
        log.info("💰 Số tiền: {:,.0f} VNĐ", amount);
        log.info("📧 Email: {}", customerEmail);
        log.info("");

        // Bước 1: Tạo và lưu đơn hàng vào MySQL
        log.info("🔸 BƯỚC 1: Lưu đơn hàng vào MySQL...");
        Order order = new Order();
        order.setCustomerEmail(customerEmail);
        order.setProductName(productName);
        order.setAmount(amount);
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);
        long saveTime = System.currentTimeMillis() - startTime;

        log.info("✅ Đơn hàng #{} đã lưu vào MySQL ({} ms)", savedOrder.getId(), saveTime);
        log.info("");

        try {
            // Bước 2: Gửi email ĐỒNG BỘ
            log.info("🔸 BƯỚC 2: Gửi email xác nhận (ĐỒNG BỘ - API BỊ BLOCK)");
            log.warn("⚠️  ═══════════════════════════════════════════════════════════");
            log.warn("⚠️  VẤN ĐỀ: API đang bị BLOCK để đợi gửi email!");
            log.warn("⚠️  User đang phải chờ đợi...");
            log.warn("⚠️  Nếu email fail → Đơn hàng sẽ bị HUỶ!");
            log.warn("⚠️  ═══════════════════════════════════════════════════════════");
            log.info("");

            String emailContent = String.format(
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                            "   XÁC NHẬN ĐƠN HÀNG #%d\n" +
                            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                            "Sản phẩm: %s\n" +
                            "Số tiền: %,.0f VNĐ\n" +
                            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                            "Cảm ơn bạn đã mua hàng!\n" +
                            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                    savedOrder.getId(), productName, amount
            );

            emailService.sendOrderConfirmationEmail(customerEmail, savedOrder.getId(), emailContent);

            // Email thành công → Cập nhật status
            log.info("");
            log.info("🔸 BƯỚC 3: Cập nhật trạng thái đơn hàng...");
            savedOrder.setStatus(OrderStatus.SUCCESS);
            savedOrder.setEmailSent(true);

            long totalTime = System.currentTimeMillis() - startTime;
            savedOrder.setProcessingTimeMs(totalTime);

            orderRepository.save(savedOrder);

            log.info("✅ Đơn hàng #{} đã hoàn tất", savedOrder.getId());
            log.info("");
            log.info("╔═══════════════════════════════════════════════════════════════╗");
            log.info("║  ✅ THÀNH CÔNG (nhưng CHẬM)                                   ║");
            log.info("╚═══════════════════════════════════════════════════════════════╝");
            log.info("⏱️  Tổng thời gian: {} ms", totalTime);
            log.info("⚠️  User đã phải chờ {} ms!", totalTime);
            log.info("");

            return savedOrder;

        } catch (Exception e) {
            // ❌ EMAIL FAIL → ĐƠN HÀNG BỊ HUỶ!
            log.error("");
            log.error("╔═══════════════════════════════════════════════════════════════╗");
            log.error("║  ❌ THẤT BẠI - EMAIL FAIL → ĐƠN HÀNG BỊ HUỶ!                ║");
            log.error("╚═══════════════════════════════════════════════════════════════╝");
            log.error("💔 Lỗi: {}", e.getMessage());
            log.error("💔 Đơn hàng #{} bị đánh dấu FAILED", savedOrder.getId());
            log.error("");

            savedOrder.setStatus(OrderStatus.FAILED);
            savedOrder.setEmailSent(false);
            savedOrder.setErrorMessage(e.getMessage());

            long totalTime = System.currentTimeMillis() - startTime;
            savedOrder.setProcessingTimeMs(totalTime);

            orderRepository.save(savedOrder);

            log.error("⏱️  Tổng thời gian: {} ms", totalTime);
            log.error("❌ User vừa mất {} ms và KHÔNG ĐẶT ĐƯỢC HÀNG!", totalTime);
            log.error("");

            throw new RuntimeException("Không thể tạo đơn hàng: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy tất cả đơn hàng
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Lấy đơn hàng theo ID
     */
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng #" + id));
    }

    /**
     * Lấy đơn hàng theo email
     */
    public List<Order> getOrdersByEmail(String email) {
        return orderRepository.findByCustomerEmail(email);
    }

    /**
     * Lấy thống kê
     */
    public OrderStatistics getStatistics() {
        long total = orderRepository.count();
        long success = orderRepository.countByStatus(OrderStatus.SUCCESS);
        long failed = orderRepository.countByStatus(OrderStatus.FAILED);
        long pending = orderRepository.countByStatus(OrderStatus.PENDING);
        Double avgTime = orderRepository.getAverageProcessingTime();

        return new OrderStatistics(total, success, failed, pending, avgTime);
    }

    public record OrderStatistics(
            long totalOrders,
            long successOrders,
            long failedOrders,
            long pendingOrders,
            Double averageProcessingTimeMs
    ) {}
}