package com.fit.designpattern.state;

public interface OrderState {
    void handle(OrderContext order);
}
