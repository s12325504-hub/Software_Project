package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import domain.Order;

public class PaymentServiceMockTest {

    @Test
    public void testPaymentServiceUsingMockito() {

        PaymentService paymentService =
                mock(PaymentService.class);

        Order order =
                new Order(
                        "o1",
                        "c1",
                        "r1",
                        null,
                        new BigDecimal("40.00"));

        when(paymentService.processCashPayment(
                order,
                new BigDecimal("40.00")))
                .thenReturn(
                        "Payment successful. Change: 0.00");

        String result =
                paymentService.processCashPayment(
                        order,
                        new BigDecimal("40.00"));

        assertEquals(
                "Payment successful. Change: 0.00",
                result);

        verify(paymentService, times(1))
                .processCashPayment(
                        order,
                        new BigDecimal("40.00"));
    }
}