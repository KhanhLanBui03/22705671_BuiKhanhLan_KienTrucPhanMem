package com.fit.designpattern.strategy;

public class CreditCardRefund implements RefundStrategy {
    @Override
    public void refund() {
        System.out.println("Hoàn tiền qua thẻ tín dụng");
    }
}

