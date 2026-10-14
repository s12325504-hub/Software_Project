package service;

import java.math.BigDecimal;

public class DeliveryCostService {

    private static final BigDecimal RATE_PER_KM = new BigDecimal("2.00");

    public BigDecimal calculateDeliveryCost(double distance) {

        if (distance < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }

        BigDecimal deliveryDistance = BigDecimal.valueOf(distance);

        return RATE_PER_KM.multiply(deliveryDistance);
    }
}