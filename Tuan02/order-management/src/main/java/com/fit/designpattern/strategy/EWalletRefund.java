package com.fit.designpattern.strategy;

public class EWalletRefund implements RefundStrategy {
    @Override
    public void refund() {
        System.out.println("Hoàn tiền qua ví điện tử");
    }
}
