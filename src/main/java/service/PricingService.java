
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

        if (pricingStrategy == null) {
            throw new IllegalStateException("Pricing strategy cannot be null");
        }

        // Always calculate from the original order total and delivery fee.
        // This prevents discounts and surcharges from accumulating.
        BigDecimal basePrice = order.getTotalPrice();

        if (order.getDeliveryFee() != null) {
            basePrice = basePrice.add(order.getDeliveryFee());
        }

        BigDecimal newPrice = pricingStrategy.calculatePrice(basePrice);

        order.setFinalPrice(newPrice);

        return newPrice;
    }

    public void setPricingStrategy(PricingStrategy pricingStrategy) {

        if (pricingStrategy == null) {
            throw new IllegalArgumentException("Pricing strategy cannot be null");
        }

        this.pricingStrategy = pricingStrategy;
    }
}
