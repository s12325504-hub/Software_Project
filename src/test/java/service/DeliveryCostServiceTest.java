package service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class DeliveryCostServiceTest {

    @Test
    public void testCalculateDeliveryCost() {

        DeliveryCostService service =
                new DeliveryCostService();

        BigDecimal result =
                service.calculateDeliveryCost(5.0);

        assertEquals(
                new BigDecimal("10.000"),
                result);
    }

    @Test
    public void testNegativeDistanceRejected() {

        DeliveryCostService service =
                new DeliveryCostService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateDeliveryCost(-1.0));
    }
}