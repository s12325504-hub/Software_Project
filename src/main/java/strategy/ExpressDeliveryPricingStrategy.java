package strategy;

import java.math.BigDecimal;

public class ExpressDeliveryPricingStrategy implements PricingStrategy {

    private BigDecimal expressFee;

    public ExpressDeliveryPricingStrategy(BigDecimal expressFee) {
        this.expressFee = expressFee;
    }

    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        return basePrice.add(expressFee);
    }
}