package service;

import java.math.BigDecimal;

import domain.Order;

public class PaymentService {

    public String processCashPayment(Order order, BigDecimal paidAmount) {

        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        if (paidAmount == null) {
            throw new IllegalArgumentException("Paid amount cannot be null");
        }

        if (paidAmount.compareTo(order.getFinalPrice()) < 0) {
            throw new IllegalArgumentException("Insufficient payment amount");
        }

        BigDecimal change =
                paidAmount.subtract(order.getFinalPrice());

        order.setPaymentStatus("PAID");

        return "Payment successful. Change: " + change;
    }
}