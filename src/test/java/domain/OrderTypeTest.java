package domain;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

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

        LocalDateTime futureTime =
                LocalDateTime.now().plusHours(2);

        ScheduledOrder order =
                new ScheduledOrder(
                        "o3",
                        "c1",
                        "r1",
                        null,
                        new BigDecimal("40.00"),
                        futureTime);

        assertEquals(
                futureTime,
                order.getScheduledDeliveryTime());
    }

    @Test
    public void testScheduledOrderWithPastTimeRejected() {

        LocalDateTime pastTime =
                LocalDateTime.now().minusHours(2);

        assertThrows(
                IllegalArgumentException.class,
                () -> new ScheduledOrder(
                        "o4",
                        "c1",
                        "r1",
                        null,
                        new BigDecimal("40.00"),
                        pastTime));
    }
}