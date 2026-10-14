package service;

import java.math.BigDecimal;

import domain.Order;
import strategy.PricingStrategy;

public class PricingService {

    private PricingStrategy pricingStrategy;

    public PricingService(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    public BigDecimal applyPricing(Order order) {

        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        BigDecimal basePrice = order.getFinalPrice();

        BigDecimal newPrice =
                pricingStrategy.calculatePrice(basePrice);

        order.setFinalPrice(newPrice);

        return newPrice;
    }

    public void setPricingStrategy(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }
}