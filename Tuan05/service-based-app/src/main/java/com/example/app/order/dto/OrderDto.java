package com.example.app.order.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

public class OrderDto {

    @Data
    public static class CreateRequest {
        @NotBlank(message = "Tên khách hàng không được trống")
        private String customerName;

        @Email(message = "Email không hợp lệ")
        @NotBlank
        private String customerEmail;

        @NotNull
        @Positive(message = "Tổng tiền phải lớn hơn 0")
        private BigDecimal totalAmount;
    }

    @Data
    public static class Response {
        private Long id;
        private String customerName;
        private String customerEmail;
        private BigDecimal totalAmount;
        private String status;
        private String createdAt;
    }
}
