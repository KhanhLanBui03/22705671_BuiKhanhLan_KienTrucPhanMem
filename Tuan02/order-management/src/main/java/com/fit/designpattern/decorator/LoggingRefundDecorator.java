package com.fit.designpattern.decorator;

import com.fit.designpattern.strategy.RefundStrategy;

public class LoggingRefundDecorator extends RefundDecorator {

    public LoggingRefundDecorator(RefundStrategy refundStrategy) {
        super(refundStrategy);
    }

    @Override
    public void refund() {
        super.refund();
        System.out.println("Ghi log hoàn tiền");
    }
}

