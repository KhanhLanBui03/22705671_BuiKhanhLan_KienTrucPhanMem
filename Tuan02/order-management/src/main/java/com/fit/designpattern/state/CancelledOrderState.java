package com.fit.designpattern.state;

public class CancelledOrderState implements OrderState {
    @Override
    public void handle(OrderContext order) {
        System.out.println("Đơn hàng bị hủy và hoàn tiền");
    }
}

