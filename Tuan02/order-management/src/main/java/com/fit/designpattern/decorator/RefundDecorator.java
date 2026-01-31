package com.fit.designpattern.decorator;

import com.fit.designpattern.strategy.RefundStrategy;

public abstract class RefundDecorator implements RefundStrategy {

    protected RefundStrategy refundStrategy;

    public RefundDecorator(RefundStrategy refundStrategy) {
        this.refundStrategy = refundStrategy;
    }

    @Override
    public void refund() {
        refundStrategy.refund();
    }
}

