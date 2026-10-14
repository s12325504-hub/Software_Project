package strategy;

import java.math.BigDecimal;

public class NormalPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        return basePrice;
    }
}