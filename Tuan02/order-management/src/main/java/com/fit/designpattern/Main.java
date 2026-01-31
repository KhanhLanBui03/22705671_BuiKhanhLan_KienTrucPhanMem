package com.fit.designpattern;

import com.fit.designpattern.decorator.LoggingRefundDecorator;
import com.fit.designpattern.state.OrderContext;
import com.fit.designpattern.strategy.CreditCardRefund;
import com.fit.designpattern.strategy.EWalletRefund;
import com.fit.designpattern.strategy.RefundStrategy;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        // STATE
        OrderContext order = new OrderContext();
        order.process();
        order.process();
        order.process();

////        System.out.println("Đơn hàng hoàn tất, không hoàn tiền");
//        // HỦY ĐƠN + STRATEGY + DECORATOR
//        RefundStrategy refund =
//                new LoggingRefundDecorator(new EWalletRefund());
//
//        refund.refund();
    }
}
