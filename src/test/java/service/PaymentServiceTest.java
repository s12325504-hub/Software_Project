package service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import domain.Order;

public class PaymentServiceTest {

    @Test
    public void testValidCashPayment() {

        PaymentService paymentService =
                new PaymentService();

        Order order =
                new Order(
                        "o1",
                        "c1",
                        "r1",
                        null,
                        new BigDecimal("40.00"));

        String result =
                paymentService.processCashPayment(
                        order,
                        new BigDecimal("50.00"));

        assertEquals(
                "Payment successful. Change: 10.00",
                result);

        assertEquals(
                "PAID",
                order.getPaymentStatus());
    }

    @Test
    public void testInsufficientCashPaymentRejected() {

        PaymentService paymentService =
                new PaymentService();

        Order order =
                new Order(
                        "o2",
                        "c1",
                        "r1",
                        null,
                        new BigDecimal("40.00"));

        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.processCashPayment(
                        order,
                        new BigDecimal("30.00")));

        assertEquals(
                "UNPAID",
                order.getPaymentStatus());
    }
}