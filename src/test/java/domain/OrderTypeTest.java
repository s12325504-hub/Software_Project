package domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import service.DateTimeService;

public class OrderTypeTest {

    @Test
    public void testStandardOrder() {

        StandardOrder order =
                new StandardOrder(
                        "o1",
                        "c1",
                        "r1",
                        null,
                        new BigDecimal("40.00"));

        assertEquals(
                new BigDecimal("40.00"),
                order.getFinalPrice());

        assertTrue(order instanceof Order);
    }

    @Test
    public void testExpressOrderAddsExpressFee() {

        ExpressOrder order =
                new ExpressOrder(
                        "o2",
                        "c1",
                        "r1",
                        null,
                        new BigDecimal("40.00"),
                        new BigDecimal("5.00"));

        assertEquals(
                new BigDecimal("5.00"),
                order.getExpressFee());

        assertEquals(
                new BigDecimal("45.00"),
                order.getFinalPrice());
    }

    @Test
    public void testScheduledOrderWithFutureTime() {

        LocalDateTime currentTime =
                LocalDateTime.of(
                        2026,
                        10,
                        15,
                        10,
                        0);

        LocalDateTime futureTime =
                LocalDateTime.of(
                        2026,
                        10,
                        15,
                        12,
                        0);

        DateTimeService dateTimeService =
                mock(DateTimeService.class);

        when(dateTimeService.now())
                .thenReturn(currentTime);

        ScheduledOrder order =
                new ScheduledOrder(
                        "o3",
                        "c1",
                        "r1",
                        null,
                        new BigDecimal("40.00"),
                        futureTime,
                        dateTimeService);

        assertEquals(
                futureTime,
                order.getScheduledDeliveryTime());

        verify(dateTimeService, times(1))
                .now();
    }

    @Test
    public void testScheduledOrderWithPastTimeRejected() {

        LocalDateTime currentTime =
                LocalDateTime.of(
                        2026,
                        10,
                        15,
                        10,
                        0);

        LocalDateTime pastTime =
                LocalDateTime.of(
                        2026,
                        10,
                        15,
                        8,
                        0);

        DateTimeService dateTimeService =
                mock(DateTimeService.class);

        when(dateTimeService.now())
                .thenReturn(currentTime);

        assertThrows(
                IllegalArgumentException.class,
                () -> new ScheduledOrder(
                        "o4",
                        "c1",
                        "r1",
                        null,
                        new BigDecimal("40.00"),
                        pastTime,
                        dateTimeService));

        verify(dateTimeService, times(1))
                .now();
    }
}