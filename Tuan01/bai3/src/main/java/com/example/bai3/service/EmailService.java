package com.example.bai3.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class EmailService {

    private final Random random = new Random();

    /**
     * Gửi email xác nhận đơn hàng
     *
     * Simulation:
     * - Mất 3-5 giây để gửi (giả lập network delay)
     * - Có 30% khả năng thất bại (giả lập email service không ổn định)
     */
    public void sendOrderConfirmationEmail(String email, Long orderId, String orderDetails) {
        log.info("📧 ═══════════════════════════════════════════════");
        log.info("📧 BẮT ĐẦU GỬI EMAIL");
        log.info("📧 Người nhận: {}", email);
        log.info("📧 Đơn hàng: #{}", orderId);
        log.info("📧 ═══════════════════════════════════════════════");

        try {
            // Simulate email sending delay (3-5 seconds)
            int delayMs = 3000 + random.nextInt(2000);
            log.warn("⏱️  ĐỢI GỬI EMAIL... (sẽ mất ~{}ms)", delayMs);
            log.warn("⚠️  API BỊ BLOCK Ở ĐÂY - User đang đợi...");

            Thread.sleep(delayMs);

            // Simulate 30% failure rate
            boolean willFail = random.nextInt(100) < 30;

            if (willFail) {
                log.error("❌ ═══════════════════════════════════════════════");
                log.error("❌ EMAIL GỬI THẤT BẠI!");
                log.error("❌ Lỗi: SMTP Connection Timeout");
                log.error("❌ Email service không phản hồi");
                log.error("❌ ═══════════════════════════════════════════════");
                throw new RuntimeException("SMTP Connection Timeout - Email service không phản hồi");
            }

            log.info("✅ ═══════════════════════════════════════════════");
            log.info("✅ EMAIL ĐÃ GỬI THÀNH CÔNG!");
            log.info("✅ Người nhận: {}", email);
            log.info("✅ Thời gian: {}ms", delayMs);
            log.info("✅ ═══════════════════════════════════════════════");
            log.info("📧 Nội dung email:");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.info(orderDetails);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("❌ Email bị interrupt: {}", e.getMessage());
            throw new RuntimeException("Email sending bị interrupt", e);
        } catch (RuntimeException e) {
            log.error("❌ Lỗi gửi email: {}", e.getMessage());
            throw e;
        }
    }
    /**
     * Kiểm tra email service có hoạt động không
     */
    public boolean isEmailServiceAvailable() {
        // Giả lập: 95% thời gian service hoạt động
        return random.nextInt(100) < 95;
    }
}