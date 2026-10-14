package service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import domain.Order;
import strategy.ExpressDeliveryPricingStrategy;
import strategy.NormalPricingStrategy;
import strategy.PeakHourPricingStrategy;
import strategy.PromotionalPricingStrategy;

public class PricingServiceTest {

    @Test
    public void testNormalPricing() {

        Order order =
                new Order(
                        "o1",
                        "c1",
                        "r1",
                        null,
                        new BigDecimal("40.00"));

        PricingService pricingService =
                new PricingService(
                        new NormalPricingStrategy());

        BigDecimal result =
                pricingService.applyPricing(order);

        assertEquals(
                new BigDecimal("40.00"),
                result);
    }

    @Test
    public void testPeakHourPricing() {

        Order order =
                new Order(
                        "o2",
                        "c1",
                        "r1",
                        null,
                        new BigDecimal("40.00"));

        PricingService pricingService =
                new PricingService(
                        new PeakHourPricingStrategy(
                                new BigDecimal("25")));

        BigDecimal result =
                pricingService.applyPricing(order);

        assertEquals(
                new BigDecimal("50.00"),
                result);
    }

    @Test
    public void testPromotionalPricing() {

        Order order =
                new Order(
                        "o3",
                        "c1",
                        "r1",
                        null,
                        new BigDecimal("40.00"));

        PricingService pricingService =
                new PricingService(
                        new PromotionalPricingStrategy(
                                new BigDecimal("25")));

        BigDecimal result =
                pricingService.applyPricing(order);

        assertEquals(
                new BigDecimal("30.00"),
                result);
    }

    @Test
    public void testExpressDeliveryPricing() {

        Order order =
                new Order(
                        "o4",
                        "c1",
                        "r1",
                        null,
                        new BigDecimal("40.00"));

        PricingService pricingService =
                new PricingService(
                        new ExpressDeliveryPricingStrategy(
                                new BigDecimal("5.00")));

        BigDecimal result =
                pricingService.applyPricing(order);

        assertEquals(
                new BigDecimal("45.00"),
                result);
    }
}