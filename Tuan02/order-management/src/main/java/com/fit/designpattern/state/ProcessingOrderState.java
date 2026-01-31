package com.fit.designpattern.state;

public class ProcessingOrderState implements OrderState {
    @Override
    public void handle(OrderContext order) {
        System.out.println("Đóng gói và vận chuyển");
        order.setState(new DeliveredOrderState());
    }
}
