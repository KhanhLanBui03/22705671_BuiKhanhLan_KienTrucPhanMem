package com.fit.designpattern.state;

public class DeliveredOrderState implements OrderState {
    @Override
    public void handle(OrderContext order) {
        System.out.println("Đơn hàng đã được giao thành công");
    }
}

